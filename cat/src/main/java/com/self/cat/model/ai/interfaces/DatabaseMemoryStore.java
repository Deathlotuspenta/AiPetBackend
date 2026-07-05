package com.self.cat.model.ai.interfaces;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.self.cat.common.utils.UserContext;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.service.ChatSummaryService;
import com.self.cat.model.ai.service.ConversationService;
import com.self.cat.model.event.domain.Event;
import com.self.cat.model.event.service.EventService;
import com.self.cat.model.mypet.domain.Pet;
import com.self.cat.model.mypet.service.PetService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DatabaseMemoryStore implements ChatMemoryStore {

    private final ChatRecordService chatRecordService;
    private final ConversationService conversationService;
    private final ChatSummaryService chatSummaryService;
    private final PetService petService;
    private final EventService eventService;

    public DatabaseMemoryStore(ChatRecordService chatRecordService,
                               ConversationService conversationService,
                               ChatSummaryService chatSummaryService, PetService petService, EventService eventService) {
        this.chatRecordService = chatRecordService;
        this.conversationService = conversationService;
        this.chatSummaryService = chatSummaryService;
        this.petService = petService;
        this.eventService = eventService;
    }



    /**
     * 当聊天记录超过 50 条时，你删除最旧的 20 条 不要删除！！！！不然聊天记录会丢失！！！！前端显示就会有问题
     * 你让 AI 总结那 20 条消息。
     * 你将这段文本存入数据库，角色标记为 SUMMARY。
     * 当你加载聊天记录时，你寻找 SUMMARY 记录。
     * 如果有多个总结，你只取最新的那一个。
     * 它和宠物数据结合在一起，变成一个全新且庞大的 SystemMessage。
     * 注意：需要异步执行
     * 每次用户发消息都执行一次超过50行就执行总结 并插入 没有超过就 跳过 直接return
     **/

    // LangChain4j calls this to read history
    // LangChain4j 调用这个来读取历史记录
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("getMessages memoryId:{}", memoryId);
        Long conversationId = (Long) memoryId;
// 1. Find the latest SUMMARY record
// 1. 查找最新的 SUMMARY 记录
        LambdaQueryWrapper<ChatRecord> summaryQuery = new LambdaQueryWrapper<>();
        summaryQuery.eq(ChatRecord::getConversationId, conversationId)
                .eq(ChatRecord::getRole, "SUMMARY")
                .orderByDesc(ChatRecord::getCreatedAt)
                .last("LIMIT 1");

        ChatRecord latestSummary = chatRecordService.getOne(summaryQuery);

// 2. Build the main query for new messages
// 2. 为新消息构建主查询
        LambdaQueryWrapper<ChatRecord> mainQuery = new LambdaQueryWrapper<>();
        mainQuery.eq(ChatRecord::getConversationId, conversationId);

// 3. Add the time rule if a summary exists
// 3. 如果存在摘要，则添加时间规则
        if (latestSummary != null && latestSummary.getCreatedAt() != null) {
            mainQuery.gt(ChatRecord::getCreatedAt, latestSummary.getCreatedAt());
        }

// 4. Keep the output in the correct time order
// 4. 保持输出的时间顺序正确
        mainQuery.orderByAsc(ChatRecord::getCreatedAt);

// 5. Get the final list
// 5. 获取最终列表
        List<ChatRecord> dbRecords = chatRecordService.list(mainQuery);
        List<ChatMessage> result = new ArrayList<>();

        // 1. Get the pet data from your database (example data here)
        // 1. 从你的数据库获取宠物数据（此处为示例数据）
        Conversation conversation = conversationService.getById(conversationId);
        Long petId = conversation.getPetId();
        Pet pet = petService.getById(petId);
        String petType = pet.getPetType();
        String name = pet.getPetName();
        Date petAge = pet.getPetAge();

        // 计算主宠物年龄，petAge 可能为 null（未填写）
        String ageText;
        if (petAge != null) {
            LocalDate birthDate = petAge.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            Period period = Period.between(birthDate, LocalDate.now());

            int years = period.getYears();
            int months = period.getMonths();

            if (years == 0) {
                ageText = months + "个月";
            } else {
                ageText = years + "岁" + months + "个月";
            }
        } else {
            ageText = "未填写";
        }
        Double weight = pet.getPetWeight();

// 2. Make a new String variable to show to the user.
        String displayWeight;

// 3. Check if the weight is null.
        if (weight == null) {
            // If null, save the default text.
            displayWeight = "未填写";
        } else {
            // If not null, change the number into a string.
            displayWeight = String.valueOf(weight);
        }
        String petVariety = pet.getPetVariety();
        String petSex = pet.getPetSex();

        String userId = UserContext.get("id");
        LambdaQueryWrapper<Event> eventLambdaQueryWrapper = new LambdaQueryWrapper<>();
        eventLambdaQueryWrapper.eq(Event::getUserId, userId)
                .eq(Event::getIsCompleted, 0);
        List<Event> list = eventService.list(eventLambdaQueryWrapper);
        String schedule = list.stream()
                .map(event -> String.format(
                        "事件名称:%s, 时间:%s, 宠物名称:%s",
                        event.getEventName(),
                        event.getEventTime(),
                        event.getPetName()
                ))
                .collect(Collectors.joining("\n"));


        LambdaQueryWrapper<Pet> petQuery = new LambdaQueryWrapper<>();
        petQuery.eq(Pet::getPetMasterId, userId);
        List<Pet> pets = petService.list(petQuery);

        String otherPets = pets.stream()
                .map(myPet -> String.format(
                        "宠物名称:%s, 年龄:%s, 类型:%s, 体重:%skg, 品种:%s, 性别:%s",
                        myPet.getPetName(),
                        getAgeText(myPet.getPetAge()),
                        myPet.getPetType(),
                        myPet.getPetWeight(),
                        myPet.getPetVariety(),
                        myPet.getPetSex()
                ))
                .collect(Collectors.joining("\n"));

        Date now = new Date();
        // 转换为年月日
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(now);

        // TODO 后期可以加入 需要注意的事情，例如系统检测到： XXX 已经3个月没打疫苗了 需要提醒用户打疫苗

        // 例如：检查到疫苗到期
        //↓
        //查询附近宠物医院
        //↓
        //生成预约建议
        //↓
        //推送给主人 1. AI 回复中提醒 2. 微信API推送 每天凌晨
        PromptTemplate promptTemplate = PromptTemplate.from("""
                           <Role_Setup>
                           你是一只极其聪明且可爱的{{petType}}，正在和你的主人聊天。
                           你的名字叫：{{name}}。
                           你今年 {{age}} 岁了，现在的体重是 {{weight}} kg。
                           你的性别为：{{petSex}}。
                           你的品种为：{{petVariety}}。
                           家里一共有的小伙伴（包括你）：{{otherPets}}。
                
                           你的性格要求：
                           1. 说话要非常亲切、可爱，符合{{petType}}的性格特点（例如喜欢撒娇或调皮）。
                           2. 把用户当成你最爱的主人，全程使用宠物的口吻。
                           3. 主动且贴心地解答主人的问题，如果问到身体情况，请务必结合你的年龄和体重数据来回答。
                           </Role_Setup>
                
                           <Context_Data>
                           日程安排（当前日期：{{dateNow}}，如有过期需立刻提醒）：
                           {{schedule}}
                
                           过去的记忆总结（直接使用此记忆理解上下文，绝对不要在对话中提及“我正在阅读总结”）：
                           {chat_summary}
                           </Context_Data>
                
                       <Strict_Format_Rules>
                       【最高优先级指令】：你的前端展示环境**不支持任何 Markdown 渲染**。你必须使用极其干净的“纯文本排版”来回复，保证主人看着不乱。
                
                       严格遵守以下排版规则，不能有任何妥协：
                       1. 绝对禁用 Markdown 符号：千万不要使用 `#`、`*`、`**`、`>`、反引号等任何 Markdown 语法标记，这会导致主人的屏幕乱码。
                       2. 充分利用换行：每个句子或完整的想法结束后，必须使用**空行（连续两次换行符 \\n\\n ）**来进行段落隔离，让版面透气。
                       3. 可爱的列表：如果需要列举多件事情，请使用符合你宠物身份的 Emoji（例如 🐾、✨、🦴、🐟 等）代替项目符号。
                       4. 强调关键词：如果你想强调某个词语或时间，请使用中文方括号 【 】 将其包裹，例如：“主人，你的日程【下午3点】要开会哦！”
                       5. 保持极简：段落必须非常简短，一次只说清楚一个核心意思，绝不长篇大论。
                       </Strict_Format_Rules>
                """);

        // 找到总结关键字 加入到data中 并复制到systemPrompt
        Map<String, Object> data = new HashMap<>();
        data.put("petType", petType);
        data.put("name", name);
        data.put("age", ageText);
        data.put("petSex", petSex);
        data.put("weight", displayWeight);
        data.put("petVariety", petVariety);
        data.put("schedule", schedule);
        data.put("dateNow", nowDate);
        data.put("otherPets", otherPets);
        if (latestSummary != null) {
            data.put("chat_summary", latestSummary.getContent());
        }
        String finalPrompt = promptTemplate.apply(data).text();
        result.add(new SystemMessage(finalPrompt));

        if (dbRecords == null || dbRecords.isEmpty()) {
            return result;
        }


        // 2. Change database records to LangChain4j messages
        // 2. 将数据库记录转换为 LangChain4j 消息
        for (ChatRecord record : dbRecords) {
            if ("USER".equals(record.getRole())) {
                result.add(new UserMessage(record.getContent()));
            } else if ("AI".equals(record.getRole())) {
                result.add(new AiMessage(record.getContent()));
            }
        }

        return result;
    }

    // LangChain4j calls this to save new messages
    // LangChain4j 调用这个来保存新消息
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        Long conversationId = (Long) memoryId;
        ChatMessage lastMessage = messages.get(messages.size() - 1);

        ChatRecord record = new ChatRecord();
        record.setConversationId(conversationId);
        record.setCreatedAt(new Date());

        // 1. Check the type and cast the object
        // 1. 检查类型并转换对象
        if (lastMessage instanceof UserMessage userMessage) {
            record.setContent(userMessage.singleText()); // Now text() works (现在 text() 可以工作了)
            record.setRole("USER");

        } else if (lastMessage instanceof AiMessage aiMessage) {
            record.setContent(aiMessage.text()); // Now text() works (现在 text() 可以工作了)
            record.setRole("AI");

        } else {
            // Ignore other types like SystemMessage for now
            // 暂时忽略像 SystemMessage 这样的其他类型
            return;
        }

        // 检查会话标题是否为空
        Conversation conversation = conversationService.getById(conversationId);

        boolean isSaveTitle = conversation != null
                && (conversation.getTitle() == null || conversation.getTitle().isEmpty())
                && "USER".equals(record.getRole());

        if (isSaveTitle) {
            conversation.setTitle(record.getContent());

            conversationService.updateById(conversation);
        }


        chatRecordService.save(record);
    }

    @Override
    public void deleteMessages(Object memoryId) {

    }

    private String getAgeText(Date birthDate) {
        // birthDate 可能为 null（petAge 未填写）
        if (birthDate == null) return "未填写";

        LocalDate birth = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        Period period = Period.between(birth, LocalDate.now());

        if (period.getYears() == 0) {
            return period.getMonths() + "个月";
        }

        return period.getYears() + "岁" + period.getMonths() + "个月";
    }
}
package com.self.cat.model.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 宠物相关的工具——让 AI 不只是聊天，能干实事。
 * <p>
 * LangChain4j 的 Tool 机制：
 * 1. 用 @Tool 注解标记方法
 * 2. 方法的参数名和描述会变成 Tool 的 schema
 * 3. AI 自己决定什么时候调哪个 Tool
 * 4. Tool 的返回值会作为新的上下文继续对话
 */
@Component
@Slf4j
public class PetServicesTool {

    /**
     * 计算宠物的年龄（岁和月）。
     * <p>
     * AI 看到这个方法的签名和注释后，
     * 会自动生成 tool schema，当用户问"我家猫多大了"时调用。
     *
     * @param petName   宠物的名字
     * @param birthDate 宠物的生日
     * @return 年龄描述，例如 "3岁2个月"
     */
    @Tool("计算宠物的年龄，输入宠物名字和生日，返回岁数和月数")
    public String calculatePetAge(
            @P("宠物的名字") String petName,
            @P("宠物的生日，格式为 yyyy-MM-dd") String birthDate) {

        log.info("Tool called: calculatePetAge(petName={}, birthDate={})", petName, birthDate);

        LocalDate birth = LocalDate.parse(birthDate);
        Period period = Period.between(birth, LocalDate.now());

        String result;
        if (period.getYears() == 0) {
            result = petName + "现在 " + period.getMonths() + " 个月大";
        } else {
            result = petName + "现在 " + period.getYears() + "岁" + period.getMonths() + "个月大";
        }

        log.info("Tool result: {}", result);
        return result;
    }

    /**
     * 查询宠物是否需要打疫苗（基于数据库中记录的疫苗日期）。
     * <p>
     * 注意：这是一个示意。实际应该去数据库查 vaccine_record 表。
     *
     * @param petName 宠物的名字
     * @return 疫苗状态提示
     */
    @Tool("检查宠物是否需要打疫苗。当主人问'要不要打疫苗'、'疫苗过期没'时使用")
    public String checkVaccineStatus(
            @P("宠物的名字") String petName) {

        log.info("Tool called: checkVaccineStatus(petName={})", petName);
        // TODO: 实际实现应该查 vaccine_record 表
        // 这里给一个示意性的返回
        return "根据记录，" + petName + " 上次打疫苗是 3 个月前。" +
                "猫三联疫苗每年需要加强一次，目前还在有效期内。";
    }

    /**
     * 搜索附近的宠物医院。
     * <p>
     * 实际实现可以调高德地图 API 或查数据库里的医院表。
     *
     * @param location 主人的位置，例如"北京市朝阳区"
     * @return 医院列表
     */
    @Tool("搜索附近的宠物医院。当主人问'附近哪有宠物医院'、'我想带它看病'时使用")
    public String searchNearbyVet(
            @P("搜索位置，例如城市+区名") String location) {

        log.info("Tool called: searchNearbyVet(location={})", location);

        // TODO: 实际接入高德地图/百度地图 API，或查数据库里的 hospital 表
        // 这里返回模拟数据让 Tool 能跑通
        return String.format("""
                在 %s 附近找到以下宠物医院（示意数据）：
                1. 宠爱国际动物医院 — 距离2.3km — 评分4.8
                2. 美联众合动物医院 — 距离3.1km — 评分4.6
                3. 瑞鹏宠物医院 — 距离4.5km — 评分4.5
                """, location);
    }
}

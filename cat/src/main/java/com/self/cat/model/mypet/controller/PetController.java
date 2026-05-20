package com.self.cat.model.mypet.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.exception.PetException;
import com.self.cat.common.http.HttpResult;
import com.self.cat.model.mypet.domain.Pet;
import com.self.cat.model.mypet.service.PetService;
import com.self.cat.model.owner.domain.dto.SavePetInformationDto;
import com.self.cat.model.owner.domain.dto.UpdatePetInformationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pet")
@Tag(name = "宠物管理",description = "宠物管理API")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping("/getMyPetList")
    @Operation(summary = "获取我的宠物列表")
    public HttpResult<List<Pet>> getMyPetList() {
        int id = 1;
        return HttpResult.success(petService.getMyPetList(id));
    }

    @PostMapping("/saveMyPetInformation")
    @Operation(summary = "Save my pet Information")
    public HttpResult<String> savePet(@RequestBody SavePetInformationDto pet) {
        boolean b = petService.savePet(pet);
        if (b) {
            return HttpResult.success("保存成功");
        }
        return HttpResult.error(ResultCode.SAVE_PET_ERROR.getCode(), ResultCode.SAVE_PET_ERROR.getMessage());
    }

    @DeleteMapping("/deleteMyPetInformation/{id}")
    @Operation(summary = "删除我的宠物信息")
    public HttpResult<String> deletePet(@PathVariable Integer id) {
        boolean b = petService.removeById(id);
        if (b) {
            return HttpResult.success("删除成功");
        }
        return HttpResult.error(ResultCode.DELETE_PER_ERROR.getCode(), ResultCode.DELETE_PER_ERROR.getMessage());
    }

    @PutMapping("/updateMyPetInformation/{id}")
    @Operation(summary = "更新我的宠物信息")
    public HttpResult<String> updatePet(@PathVariable Integer id, @RequestBody UpdatePetInformationDto pet) {
        Pet p = petService.getById(id);
        if (p == null) {
            HttpResult.error(ResultCode.ERROR.getCode(), "没有找到该宠物");
        }
        BeanUtils.copyProperties(pet, p);
        boolean b = petService.updateById(p);
        if (b) {
            return HttpResult.success("更新成功");
        }
        return HttpResult.error(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }
}

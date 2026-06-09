package com.self.cat.model.mypet.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.common.utils.UserContext;
import com.self.cat.model.mypet.domain.Pet;
import com.self.cat.model.mypet.service.PetService;
import com.self.cat.model.mypet.mapper.PetMapper;
import com.self.cat.model.owner.controller.UserController;
import com.self.cat.model.owner.domain.dto.SavePetInformationDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【pet】的数据库操作Service实现
* @createDate 2026-05-14 15:26:40
*/
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet>
    implements PetService{

    @Override
    public List<Pet> getMyPetList(Integer id) {
        return this.lambdaQuery().eq(Pet::getPetMasterId, id).list();
    }

    @Override
    public boolean savePet(SavePetInformationDto pet) {
        String id = UserContext.get("id");
        Pet p = new Pet();
        BeanUtils.copyProperties(pet, p);
        Date date = new Date();
        p.setCreateTime(date);
        p.setUpdateTime(date);
        p.setPetMasterId(Integer.valueOf(id));
        return this.save(p);
    }
}





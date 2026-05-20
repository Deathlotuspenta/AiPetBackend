package com.self.cat.model.mypet.service;

import com.self.cat.model.mypet.domain.Pet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.self.cat.model.owner.domain.dto.SavePetInformationDto;

import java.util.List;

/**
* @author Administrator
* @description 针对表【pet】的数据库操作Service
* @createDate 2026-05-14 15:26:40
*/
public interface PetService extends IService<Pet> {

    List<Pet> getMyPetList(Integer id);

    boolean savePet(SavePetInformationDto pet);
}

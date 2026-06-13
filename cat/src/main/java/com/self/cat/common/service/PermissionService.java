package com.self.cat.common.service;

import com.self.cat.common.enums.SourceName;
import com.self.cat.model.event.service.EventService;
import com.self.cat.model.mypet.service.PetService;
import com.self.cat.model.owner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private EventService eventService;

    /**
     * 检查用户是否具有访问特定资源的权限
     *
     * @param userId 用户ID
     * @param resource 资源标识
     * @return 是否具有访问权限
     */
    public boolean hasPermission(Long userId, SourceName  resource,Long resourceId) {

        return switch (resource) {
            case PET -> petService.hasPermission(userId, resourceId);
            case EVENT -> eventService.hasPermission(userId, resourceId);
            default -> false;
        };
    }
}

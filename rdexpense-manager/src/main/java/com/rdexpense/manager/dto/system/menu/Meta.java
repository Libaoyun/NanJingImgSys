package com.rdexpense.manager.dto.system.menu;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/15 17:24
 */
@Data
public class Meta<T> implements Serializable {
    private String title;
    private Boolean keepAlive = false;
    private List<T> btnPermissions = new ArrayList<>();
}

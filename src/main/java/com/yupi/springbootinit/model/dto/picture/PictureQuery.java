package com.yupi.springbootinit.model.dto.picture;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode( callSuper = true )
@Data
public class PictureQuery extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String searchText;
}

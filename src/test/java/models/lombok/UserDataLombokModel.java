package models.lombok;

import lombok.Data;

@Data
public class UserDataLombokModel {
    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
}
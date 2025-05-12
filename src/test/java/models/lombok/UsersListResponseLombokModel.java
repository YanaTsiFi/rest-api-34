package models.lombok;

import lombok.Data;
import java.util.List;

@Data
public class UsersListResponseLombokModel {
    private Integer page;
    private Integer per_page;
    private Integer total;
    private Integer total_pages;
    private List<UserDataLombokModel> data;
    private Support support;
}

@Data
class Support {
    private String url;
    private String text;
}
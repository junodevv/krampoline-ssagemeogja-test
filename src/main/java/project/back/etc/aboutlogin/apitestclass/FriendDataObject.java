package project.back.etc.aboutlogin.apitestclass;

import lombok.Data;
import project.back.etc.aboutlogin.apitestclass.Friend;

import java.util.List;

@Data
public class FriendDataObject {

    private String after_url;

    private List<Friend> elements;

    private Long favorite_count;
    private Long total_count;
}
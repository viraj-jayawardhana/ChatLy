package utils;

import com.decima.blogger.R;
import com.decima.blogger.model.UserModel;

import java.util.Arrays;
import java.util.List;

public class Constance {

    public static final String EXTRA_KEY_DATA = "data_key";

    public static List<UserModel> _chat_users = Arrays.asList(
            new UserModel("Sajani Samarakkodi", R.drawable.user_icon_2, 1),
            new UserModel("Uresha Ravihari", R.drawable.user_icon_4, 2),
            new UserModel("Supun Arosh", R.drawable.user_icon_1, 3),
            new UserModel("Meven Silva", R.drawable.user_icon_3, 4),
            new UserModel("Kasun Kalhara", R.drawable.user_icon_6, 5),
            new UserModel("Saduni Bhagya", R.drawable.user_icon_5, 6)
    );

    public static final int _MESSAGE_TYPE_TEXT = 1;
    public static final int _MESSAGE_TYPE_IMAGES = 2;

}

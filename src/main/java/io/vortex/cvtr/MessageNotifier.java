package io.vortex.cvtr;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
// todo:lithiumnzinc 2024/8/14 18:22 > 完善一下逻辑
public class MessageNotifier {

    private static final String defaultDialogTitle = "Message Notification";

    private static final Icon defaultInformationIcon = Messages.getInformationIcon();

    public static void showMessageByDialog(String msg) {
        showMessageByDialog(msg, defaultDialogTitle);
    }

    public static void showMessageByDialog(String msg, String title) {
        showMessageByDialog(msg, title, defaultInformationIcon);
    }

    public static void showMessageByDialog(String msg, String title, Icon icon) {
        Messages.showMessageDialog(msg, title, icon);
    }

}

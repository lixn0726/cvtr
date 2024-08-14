package io.vortex.cvtr;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationInfo;
import io.vortex.cvtr.process.data.MethodProcessContext;

public class ProjectNotification {

    private static final boolean curIdeaVersionIsLaterThan2020_3;

    private static final String versionNumber2020_3 = "2020.3";

    @SuppressWarnings("all")
    private static final NotificationGroup notificationGroupForPrevious =
            new NotificationGroup("CvtrNotificationGroup", NotificationDisplayType.BALLOON, true);

    private static final NotificationGroup notificationGroupForLater = NotificationGroupManager.getInstance()
            .getNotificationGroup("CvtrNotificationGroup");

    static {
        ApplicationInfo curAppInfo = ApplicationInfo.getInstance();
        String versionNumber = curAppInfo.getFullVersion();
        curIdeaVersionIsLaterThan2020_3 = versionNumber.compareTo(versionNumber2020_3) > 0;
    }

    public static void notifyWarning(MethodProcessContext ctx, String title, String message) {
        doNotify(ctx, title, message, NotificationType.WARNING);
    }

    public static void notifyInfo(MethodProcessContext ctx, String title, String message) {
        doNotify(ctx, title, message, NotificationType.INFORMATION);
    }

    public static void notifyError(MethodProcessContext ctx, String title, String message) {
        doNotify(ctx, title, message, NotificationType.ERROR);
    }

    private static void doNotify(MethodProcessContext ctx, String title, String message, NotificationType type) {
        if (curIdeaVersionIsLaterThan2020_3) {
            notificationGroupForLater.createNotification(title, message, type)
                    .notify(ctx.getMethod().getProject());
        } else {
            notificationGroupForPrevious.createNotification(title, message, type)
                    .notify(ctx.getMethod().getProject());
        }
    }
}

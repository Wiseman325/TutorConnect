package ell.one.tutorlink.database_handlers;

import java.util.List;
public interface SubjectListCallback {
    void onSubjectListReceived(List<String> subjects);
}

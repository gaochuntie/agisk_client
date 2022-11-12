package atms.app.my_application_c.ConfigBox;


//TODO Xmlprocessor
public class FileAction extends ActionBase{
    public enum File_ActionType{
        FILE_ACTION_COPY,
        FILE_ACTION_DELETE,
        FILE_ACTION_COMPRESS,
        FILE_ACTION_DEPRESS
    }

    File_ActionType type;
    int argc=0;
    String argv[]=null;

    @Override
    public boolean doAction() {
        switch (type) {
            case FILE_ACTION_DELETE:
                break;
            case FILE_ACTION_COPY:
                break;
            case FILE_ACTION_COMPRESS:
                break;
            case FILE_ACTION_DEPRESS:
                break;
            default:
                break;

        }
        return false;
    }

    public FileAction(int argc, File_ActionType _type, String[] args) {
        this.actiontype = ActionType.ACTION_TYPE_FILE;
        this.argc = argc;
        type = _type;
        this.argv = args;



    }
}

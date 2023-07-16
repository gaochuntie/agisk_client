package atms.app.agiskclient.GPTfdisk;

public class PartType {
    public enum Part_Type {
        TYPE_FREESPACE,
        TYPE_TODOPART
    }

    private Part_Type part_type;

    //TODO Add full part type support
    public PartType(String typeName) {
        switch (typeName) {
            case "*FREESPACE":
                part_type = Part_Type.TYPE_FREESPACE;
                break;
            default:
                part_type = Part_Type.TYPE_TODOPART;
                break;
        }
    }

    public Part_Type getPartType() {
        return this.part_type;
    }
}

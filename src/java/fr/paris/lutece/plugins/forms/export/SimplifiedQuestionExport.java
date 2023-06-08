package fr.paris.lutece.plugins.forms.export;

public class SimplifiedQuestionExport {

    public Integer exportDisplayOrder;
    public Integer id;
    public String stepTitle;
    public String title;
    public Boolean exportable;
    public Boolean exportablePdf;

    public SimplifiedQuestionExport(Integer exportDisplayOrder, Integer id, String stepTitle, String title, Boolean exportable, Boolean exportablePdf) {
        this.exportDisplayOrder = exportDisplayOrder;
        this.id = id;
        this.stepTitle = stepTitle;
        this.title = title;
        this.exportable = exportable;
        this.exportablePdf = exportablePdf;
    }

    // CONVERT TO JSON
    public String toJson() {
        return "{"
                + "\"id\":" + id + ","
                + "\"exportDisplayOrder\":" + exportDisplayOrder + ","
                + "\"stepTitle\":\"" + stepTitle + "\","
                + "\"title\":\"" + title + "\","
                + "\"exportable\":" + exportable + ","
                + "\"exportablePdf\":" + exportablePdf
                + "}";
    }
}

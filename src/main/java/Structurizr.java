import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Enterprise;
import com.structurizr.view.*;

public class Structurizr {

    public static void main(String[] args) throws Exception {
        // Workspace
        Workspace workspace = new Workspace("Private apps", "This is a model of my private systems built from many apps");
        ViewSet views = workspace.getViews();
        Model model = workspace.getModel();
        model.setEnterprise(new Enterprise("Sebastian Czech"));

        // C1
        Person user = model.addPerson("User", "Me, a user of my software system.");
        SoftwareSystem notesApp = model.addSoftwareSystem("Notes", "Notes app.");
        user.uses(notesApp, "Uses");
        SoftwareSystem multimediaApp = model.addSoftwareSystem("Multimedia", "Multimedia app.");
        user.uses(multimediaApp, "Uses");
        SoftwareSystem apiRest = model.addSoftwareSystem("API", "REST API.");
        notesApp.uses(apiRest, "Gets / Sends");
        multimediaApp.uses(apiRest, "Gets / Sends");
        SoftwareSystem googleCalendar = model.addSoftwareSystem("Calendar", "Google Calendar.");
        notesApp.uses(googleCalendar, "Gets / Sends");
        googleCalendar.delivers(user, "Updated calendar");

        // Views generation
        SystemContextView contextView = views.createSystemContextView(notesApp, "SystemContext", "System Context diagram.");
        contextView.setPaperSize(PaperSize.A6_Landscape);
        contextView.addNearestNeighbours(apiRest);
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
        contextView.addAnimation(notesApp);
        contextView.addAnimation(multimediaApp);
        contextView.addAnimation(apiRest);
        contextView.addAnimation(googleCalendar);
        contextView.addAnimation(user);

        // C2
        Container webApp = notesApp.addContainer( "Web app", "Main UI for notes app", "Python");
        Container database = notesApp.addContainer("Database", "All data for notes", "PostgreSQL");
        user.uses(webApp, "Uses", ""); 
        webApp.uses(database, "Read / write", "PEP"); 

        // Views generation
        ContainerView containerView = views.createContainerView(notesApp, "Containers", "System containers diagram.");
        containerView.setPaperSize(PaperSize.A6_Landscape);
        containerView.addAllContainers();
        containerView.add(user);
        containerView.addAnimation(webApp);
        containerView.addAnimation(database);

        // C3
        Component authComponent = webApp.addComponent( "Authentication","Allow to authentication access to web app", "Google OAuth client");
        Component calendarComponent = webApp.addComponent("Calendar","Insert event into calendar for reminder", "Google Calendar API client");
        Component financeWizardComponent = webApp.addComponent( "Wizard for financial operations","Allow in one step to update multiples financial objects e.g. for shoping operation","Django"); 
        financeWizardComponent.uses(database, "Read / write", "JDBC");
        model.addImplicitRelationships();

        // Views generation
        ComponentView componentView = views.createComponentView(webApp,"Components","Wep app components diagram.");
        componentView.setPaperSize(PaperSize.A6_Landscape);
        componentView.add(authComponent);
        componentView.add(calendarComponent);
        componentView.add(financeWizardComponent);

        // Documentation
        StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
        template.addContextSection(notesApp, Format.Markdown,
                "# Documentation\n\n" +
                "## Notes\n\n" + 
                "## Multimedia\n\n" + 
                "## API\n\n");

        // Styles
        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
        styles.addElementStyle(Tags.PERSON).background("#ff6600").color("#ffffff").shape(Shape.Person);

        uploadWorkspaceToStructurizr(workspace);
    }

    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
        StructurizrClient structurizrClient = new StructurizrClient(Credentials.API_KEY, Credentials.API_SECRET);
        structurizrClient.putWorkspace(Credentials.WORKSPACE_ID, workspace);
    }

}
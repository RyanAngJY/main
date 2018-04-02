package seedu.recipe.ui;

import java.net.URL;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import seedu.recipe.MainApp;
import seedu.recipe.commons.core.EventsCenter;
import seedu.recipe.commons.core.LogsCenter;
import seedu.recipe.commons.core.index.Index;
import seedu.recipe.commons.events.ui.InternetSearchRequestEvent;
import seedu.recipe.commons.events.ui.JumpToListRequestEvent;
import seedu.recipe.commons.events.ui.NewResultAvailableEvent;
import seedu.recipe.commons.events.ui.RecipePanelSelectionChangedEvent;
import seedu.recipe.commons.events.ui.ShareRecipeEvent;
import seedu.recipe.commons.events.ui.UploadRecipesEvent;
import seedu.recipe.logic.commands.UploadCommand;
import seedu.recipe.model.recipe.Recipe;
import seedu.recipe.model.recipe.Url;
import seedu.recipe.ui.util.CloudStorageUtil;
import seedu.recipe.ui.util.FacebookHandler;

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    public static final String DEFAULT_PAGE_DARK = "defaultdark.html";
    public static final String DEFAULT_PAGE_LIGHT = "defaultlight.html";
    public static final String SEARCH_PAGE_URL =
            "https://se-edu.github.io/addressbook-level4/DummySearchPage.html?name=";
    private static final String FXML = "BrowserPanel.fxml";
    private static final Index FIRST_INDEX = Index.fromOneBased(1);

    private Recipe recipeToShare;
    private String uploadFilename;

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private WebView browser;

    public BrowserPanel(boolean isDarkTheme) {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage(isDarkTheme);
        registerAsAnEventHandler(this);

        setUpBrowserUrlListener();
    }

    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    private void loadRecipePage(Recipe recipe) {
        loadPage(recipe.getUrl().toString());
    }

    //@@author RyanAngJY
    /**
     * Loads the text recipe onto the browser
     */
    private void loadLocalRecipe(Recipe recipe) {
        browser.getEngine().loadContent(recipe.getRecipeInHtmlFormat());
    }
    //@@author

    /**
     * Loads a default HTML file with a background that matches the general theme.
     * @param isDarkTheme true if the app is using dark theme
     */
    public void loadDefaultPage(boolean isDarkTheme) {
        URL defaultPage;
        if (isDarkTheme) {
            defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE_DARK);
        } else {
            defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE_LIGHT);
        }
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handleRecipePanelSelectionChangedEvent(RecipePanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        Recipe recipe = event.getNewSelection().recipe;
        if (recipe.getUrl().toString().equals(Url.NULL_URL_REFERENCE)) {
            loadLocalRecipe(recipe);
        } else {
            loadRecipePage(recipe);
        }
    }

    //@@author kokonguyen191
    @Subscribe
    private void handleInternetSearchRequestEvent(InternetSearchRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        if (event.wikiaQueryHandler.getQueryNumberOfResults() != 0) {
            loadPage(event.wikiaQueryHandler.getRecipeQueryUrl());
        }
    }

    //@@author RyanAngJY
    @Subscribe
    private void handleShareRecipeEvent(ShareRecipeEvent event) {
        recipeToShare = event.getTargetRecipe();
        String urlToShare = recipeToShare.getUrl().toString();
        UiUtil.copyToClipboard(recipeToShare.getTextFormattedRecipe());

        if (!urlToShare.equals(Url.NULL_URL_REFERENCE)) {
            loadPage(FacebookHandler.getPostDomain() + recipeToShare.getUrl().toString()
                    + FacebookHandler.getRedirectEmbedded());
        } else {
            loadPage(FacebookHandler.REDIRECT_DOMAIN);
        }
    }

    /**
     * Sets up a URL listener on the browser to watch for access token.
     */
    private void setUpBrowserUrlListener() {
        WebEngine browserEngine = browser.getEngine();
        browserEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    String url = browserEngine.getLocation();
                    System.out.println("passing by");
                    if (CloudStorageUtil.checkAndSetAccessToken(url)) {
                        CloudStorageUtil.upload(uploadFilename);
                        System.out.println("a");
                        EventsCenter.getInstance().post(new NewResultAvailableEvent(UploadCommand.MESSAGE_SUCCESS));
                        EventsCenter.getInstance().post(new JumpToListRequestEvent(FIRST_INDEX));
                    }
                }
            }
        });
    }
    //@@author

    //@@author nicholasangcx
    @Subscribe
    private void handleUploadRecipesEvent(UploadRecipesEvent event) {
        loadPage(CloudStorageUtil.getAppropriateUrl());
        System.out.println("1");
        uploadFilename = event.getUploadFilename();
        if (CloudStorageUtil.hasAccessToken()) {
            CloudStorageUtil.upload(uploadFilename);
        }
    }
    //@@author
}

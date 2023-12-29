package ua.com.pragmasoft.chat.kite;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.FilePayload;
import ua.com.pragmasoft.chat.ChatPage;

import java.nio.file.Path;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class KiteChatPage implements ChatPage {
    private final Page page;
    private final Locator messages;
    private final Locator incomingMessages;
    private final Locator outgoingMessages;
    private final Locator fileAttachment;
    private final Locator input;
    private final Locator sendButton;

    private KiteChatPage(Page page) {
        this.page = page;
        Locator chat = page.locator("#kite-dialog");

        this.messages = page.locator("kite-msg");
        this.incomingMessages = page.locator("kite-msg:not([status])");
        this.outgoingMessages = page.locator("kite-msg[status]");

        Locator footer = chat.locator("kite-chat-footer");
        this.fileAttachment = footer.locator("label");
        this.input = footer.getByRole(AriaRole.TEXTBOX);
        this.sendButton = footer.locator("svg")
            .filter(new Locator.FilterOptions().setHasText("Send"));
    }

    public static KiteChatPage of(Page page, String kiteUrl) {
        page.navigate(kiteUrl);
        Locator chatButton = page.locator("#kite-toggle");
        assertThat(chatButton).isVisible();
        chatButton.click();
        return new KiteChatPage(page);
    }

    @Override
    public Page getPage() {
        return this.page;
    }

    @Override
    public String lastMessage(MessageType type) {
        return switch (type) {
            case IN -> this.incomingMessages.last().innerText();
            case OUT -> this.outgoingMessages.last().innerText();
        };
    }

    @Override
    public void sendMessage(String text) {
        this.input.fill(text);
        this.sendButton.click();
        Locator lastMessage = this.outgoingMessages.last();
        assertThat(lastMessage).hasText(Pattern.compile(text));
        this.page.waitForCondition(() -> lastMessage.getAttribute("status").equals("delivered"));
    }

    @Override
    public String uploadFile(Path pathToFile) {
        FileChooser fileChooser = this.page.waitForFileChooser(this.fileAttachment::click);
        fileChooser.setFiles(pathToFile);

        Locator lastMessage = this.outgoingMessages.last();
        Locator fileLink = lastMessage.locator("kite-file").getByRole(AriaRole.LINK);
        String fileName = pathToFile.getFileName().toString();

        assertThat(fileLink).hasAttribute("download", fileName);
        this.page.waitForCondition(() -> lastMessage.getAttribute("status").equals("delivered"),
            new Page.WaitForConditionOptions().setTimeout(25_000));
        return fileName;
    }

}
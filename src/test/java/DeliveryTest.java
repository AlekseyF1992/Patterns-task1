package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var firstMeetingDate = DataGenerator.generateDate(4);
        var secondMeetingDate = DataGenerator.generateDate(7);

        fillForm(validUser, firstMeetingDate);

        String expectedFirstText = "Встреча успешно запланирована на " + firstMeetingDate;
        SelenideElement firstNotification = $("[data-test-id='success-notification'] .notification__content");
        firstNotification.shouldBe(visible, Duration.ofSeconds(15));
        firstNotification.shouldHave(text(expectedFirstText));

        $("[data-test-id='success-notification']").shouldNotBe(visible, Duration.ofSeconds(15));

        setDateField(secondMeetingDate);

        $("[data-test-id='date'] input")
                .shouldHave(Condition.value(secondMeetingDate), Duration.ofSeconds(5));

        $$(Selectors.byText("Запланировать")).findBy(visible).click();

        $("[data-test-id='replan-notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        $("[data-test-id='replan-notification']").$(Selectors.byText("Перепланировать")).click();

        String expectedSecondText = "Встреча успешно запланирована на " + secondMeetingDate;
        $("[data-test-id='success-notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text(expectedSecondText));
    }

    private void fillForm(DataGenerator.UserInfo user, String date) {
        $("[data-test-id='city'] input").setValue(user.getCity().substring(0, 2));
        $$(".menu-item__control").findBy(exactText(user.getCity())).click();

        setDateField(date);

        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());
        $("[data-test-id='agreement']").click();

        $$(Selectors.byText("Запланировать")).findBy(visible).click();
    }

    private void setDateField(String date) {
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(date);
    }
}
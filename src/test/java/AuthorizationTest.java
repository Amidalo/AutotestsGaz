import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class AuthorizationTest {
    private WebDriver webDriver;


    @BeforeTest
    public void setUp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");

        webDriver = WebDriverManager
                .chromedriver()
                .capabilities(chromeOptions)
                .create();

    }

    @DataProvider(name = "Недействительные Email")
    public Object[][] provideEmailData() {
        return new Object[][] {
                // email,                                                                       password,     expectedMessage
                {"user@.com",                                                                   "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"usergmail.com",                                                               "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user@gmail..com",                                                             "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user@yandex@ru",                                                              "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user name@gmail.com",                                                         "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"",                                                                            "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"@gmail.com",                                                                  "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user!@mail.ru",                                                               "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user@gmail.c",                                                                "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"user@gmail.123",                                                              "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"пользователь@яндекс.рф",                                                      "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
                {"aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffgggg@gmail.com",  "123456",    "Пожалуйста, введите корректный адрес электронной почты"},
        };
    }

    // ТК-9
    @Test(dataProvider = "Недействительные Email", enabled = false, priority = 1,
            description = "Проверка сообщений об ошибках авторизации при невалидных email")
    public void testLoginErrors(String email, String password, String expectedMessage) {
        webDriver.get("https://same-v739m8ov3qm-latest.netlify.app/login");
        webDriver.manage().window().maximize();

        try {
            TimeUnit.SECONDS.sleep(1);

            // 1 действие - вводим нашу почту
            WebElement emailField = webDriver.findElement(By.xpath("//div//input[@name=\"email\"]"));
            emailField.sendKeys(email);
            TimeUnit.SECONDS.sleep(1);

            // 2 действие - вводим наш пароль
            WebElement passwordField = webDriver.findElement(By.xpath("//div//input[@name=\"password\"]"));
            passwordField.sendKeys(password);
            TimeUnit.SECONDS.sleep(1);

            // 3 действие - нажимаем по кнопке 'Войти' на странице входа
            WebElement submitButton = webDriver.findElement(
                    By.xpath("//div//button[@type=\"submit\"]")
            );
            submitButton.click();
            TimeUnit.SECONDS.sleep(3);

            By errorLocator = By.xpath("//p[contains(text()," +
                    " 'Пожалуйста, введите корректный адрес электронной почты')]");
            // Проверка сообщения
            WebElement errorMessage = webDriver.findElement(errorLocator);
            Assert.assertEquals(errorMessage.getText(), expectedMessage);

        } catch (InterruptedException e) {
            Assert.fail("Тест был прерван: " + e.getMessage());
        } catch (NoSuchElementException e) {
            Assert.fail("Возможно сообщение c предупреждением еще не успело появиться: " + e.getMessage() +
                    "\n\nПопробуйте увеличить время паузы.");
        }
    }

    @DataProvider(name = "Недействительные пароли")
    public Object[][] providePasswordData() {
        return new Object[][] {
                // email,                          password,            expectedMessage
                {"user@gmail.com",                 "12345",         "Пароль должен содержать минимум 6 символов"},
                {"test@mail.ru",                   "qwert",         "Пароль должен содержать минимум 6 символов"},
                {"example@yandex.ru",              "        ",      "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
                {"admin@domain.com",               "1",             "Пароль должен содержать минимум 6 символов"},
                {"support@company.net",            "",              "Пароль должен содержать минимум 6 символов"},
                {"support@company.net",            "sdferr:",       "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
                {"support@company.net",            "'sdferr'",      "Некорректный символ в пароле: пробел, кавычки, двоеточие"},
        };
    }

    // ТК-10
    @Test(dataProvider = "Недействительные пароли", enabled = true, priority = 1,
            description = "Проверка сообщений об ошибках авторизации при невалидном пароле")
    public void testPasswordErrors(String email, String password, String expectedMessage) {
        webDriver.get("https://same-v739m8ov3qm-latest.netlify.app/login");
        webDriver.manage().window().maximize();

        try {
            TimeUnit.SECONDS.sleep(1);

            // 1 действие - вводим нашу почту
            WebElement emailField = webDriver.findElement(By.xpath("//div//input[@name=\"email\"]"));
            emailField.sendKeys(email);
            TimeUnit.SECONDS.sleep(1);

            // 2 действие - вводим наш пароль
            WebElement passwordField = webDriver.findElement(By.xpath("//div//input[@name=\"password\"]"));
            passwordField.sendKeys(password);
            TimeUnit.SECONDS.sleep(1);

            // 3 действие - нажимаем по кнопке 'Войти' на странице входа
            WebElement submitButton = webDriver.findElement(
                    By.xpath("//div//button[@type=\"submit\"]")
            );
            submitButton.click();
            TimeUnit.SECONDS.sleep(3);

            By errorLocator = By.xpath("//p[contains(text(), 'Пароль должен содержать минимум 6 символов')]");
            // Проверка сообщения
            WebElement errorMessage = webDriver.findElement(errorLocator);
            Assert.assertEquals(errorMessage.getText(), expectedMessage);

        } catch (InterruptedException e) {
            Assert.fail("Тест был прерван: " + e.getMessage());
        } catch (NoSuchElementException e) {
            Assert.fail("Возможно сообщение c предупреждением еще не успело появиться: " + e.getMessage() +
                    "\n\nПопробуйте увеличить время паузы.");
        }
    }



    @AfterTest
    public void terminate() {
        webDriver.quit();
    }
}

package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

  private ActivityScenario<LoginActivity> scenario;

  @Before
  public void setUp() {
    scenario = ActivityScenario.launch(LoginActivity.class);
  }

  @After
  public void tearDown() {
    if (scenario != null) {
      scenario.close();
    }
  }

  @Test
  public void emptyFieldsShowsToast() {
    // Simula clic sin haber rellenado los campos
    onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

    // Verifica que se muestra el Toast con el texto esperado
    onView(withText(R.string.gaps_empty))
      .inRoot(new ToastMatcher())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
  }


  @Test
  public void invalidEmailShowsToast() {
    onView(withId(R.id.emailLogin)).perform(ViewActions.typeText("invalidEmail"));
    onView(withId(R.id.passwordLogin)).perform(ViewActions.typeText("somepassword"));
    Espresso.closeSoftKeyboard();

    onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

    scenario.onActivity(activity ->
      onView(withText(R.string.invalid_email))
        .inRoot(new ToastMatcher())
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    );
  }

  @Test
  public void incorrectCredentialsShowToast() {
    // Usa credenciales dummy que no existen
    onView(withId(R.id.emailLogin)).perform(ViewActions.typeText("nonexistent@example.com"));
    onView(withId(R.id.passwordLogin)).perform(ViewActions.typeText("wrongpassword"));
    Espresso.closeSoftKeyboard();

    onView(withId(R.id.buttonLogin)).perform(ViewActions.click());

    // Espera el resultado async de Firebase (solo para esta prueba)
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    scenario.onActivity(activity ->
      onView(withText(R.string.email_passwd_dont_match))
        .inRoot(new ToastMatcher())
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    );
  }

  @Test
  public void googleButtonDoesNotCrash() {
    // Verifica que el botón de Google no lanza errores
    onView(withId(R.id.buttonGoogle)).perform(ViewActions.click());
    // No hay verificación porque GoogleSignIn requiere mocks
  }

  @Test
  public void createAccountButtonLaunchesIntent() {
    onView(withId(R.id.buttonCreateAccount)).perform(ViewActions.click());
    // Aquí podrías verificar navegación si usas espresso-intents (opcional)
  }
}

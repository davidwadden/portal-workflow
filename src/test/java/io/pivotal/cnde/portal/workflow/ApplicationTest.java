package io.pivotal.cnde.portal.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(TestApplicationRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false",
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
})
class ApplicationTest {

  private final Shell shell;

  @Autowired
  ApplicationTest(Shell shell) {
    this.shell = shell;
  }

  @Test
  void helpCommand() {
    Object output = shell.evaluate(() -> "help");
    assertThat(output).isNotNull();
  }
}

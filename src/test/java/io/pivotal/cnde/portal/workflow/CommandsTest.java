package io.pivotal.cnde.portal.workflow;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class CommandsTest {

  private final Shell shell;

  @MockBean
  private JobService jobService;

  @Autowired
  CommandsTest(Shell shell) {
    this.shell = shell;
  }

  @Test
  void submitJob() {
    assertThat(shell.evaluate(() -> "submit-job")).isEqualTo("completed");

    verify(jobService).submitJob();
  }
}

package care.smith.top.top_phenotypic_query.c2reasoner.functions;

import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Add;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.DiffDays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionEntityTest {

  @Test
  void getType() {
    assertEquals("arithmetic", Add.get().getType());
    assertEquals("arithmetic", Add.get().getFunction().getType());

    assertEquals("date_time", DiffDays.get().getType());
    assertEquals("date_time", DiffDays.get().getFunction().getType());
  }
}

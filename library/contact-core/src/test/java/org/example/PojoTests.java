package org.example;

import com.openpojo.reflection.filters.FilterNonConcrete;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.DefaultValuesNullTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;


class PojoTests {

	@Test
	void testPojoStructureAndBehavior() {
		Validator jsonObjectsValidator = ValidatorBuilder.create()
				.with(new GetterMustExistRule())
				.with(new SetterMustExistRule())
				.with(new NoPrimitivesRule())
				.with(new GetterTester())
				.with(new SetterTester())
				.with(new DefaultValuesNullTester())
				.build();

		jsonObjectsValidator.validate("org.example", new FilterNonConcrete());
	}
}

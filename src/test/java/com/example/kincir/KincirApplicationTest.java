package com.example.kincir;

import com.example.kincir.service.AuthenticationServiceImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthenticationServiceImplTest.class,
})
class KincirApplicationTest {
}
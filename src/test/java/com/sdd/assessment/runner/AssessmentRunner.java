package com.sdd.assessment.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/resources/features", glue = "com.sdd.assessment",
    plugin = {"pretty", "json:target/cucumber.json", "html:target/cucumber.html", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"})
public final class AssessmentRunner extends AbstractTestNGCucumberTests { }

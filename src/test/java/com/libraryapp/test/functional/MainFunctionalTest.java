package com.libraryapp.test.functional;

import static com.libraryapp.test.utils.TestUtils.businessTestFile;
import static com.libraryapp.test.utils.TestUtils.currentTest;
import static com.libraryapp.test.utils.TestUtils.testReport;
import static com.libraryapp.test.utils.TestUtils.yakshaAssert;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import mainapp.MyApp;

public class MainFunctionalTest {

    @AfterAll
    public static void afterAll() {
        testReport();
    }

    @Test
    @Order(1)
    public void findMultipleTagsWithCorrectCommits() throws IOException {
        try {
            // Check if tags v1.0 and v1.1 exist
            String tagsOutput = MyApp.areTagsPresent();

            // Check that v1.0 contains the correct commit message
            String v1_0CommitMessage = MyApp.checkTagCommitMessage("v1.0", "v1.0: First stable release of TaskManager");

            // Check that v1.1 contains the correct commit message after bug fix
            String v1_1CommitMessage = MyApp.checkTagCommitMessage("v1.1", "v1.1: Bug fix in create_task function");

            // Assert that all conditions are met
            yakshaAssert(currentTest(), tagsOutput.equals("true") && v1_0CommitMessage.equals("true") && v1_1CommitMessage.equals("true"), businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(2)
    public void ifCommitWasEverRetaged() throws IOException {
        try {
            // Fetch the commit hashes for the 'v1.1' tag and check if it was ever retagged
            String commitHashes = MyApp.getCommitHashesForTag("v1.1");

            // Assert that the commit hashes for v1.1 are found and print them
            yakshaAssert(currentTest(), !commitHashes.isEmpty(), businessTestFile);
            System.out.println("Commit hashes for v1.1 tag: " + commitHashes);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }

    @Test
    @Order(3)
    public void workedOnDetachedModeOrNot() throws IOException {
        try {
            // Check if there's a commit stating "moving from main to v1.0"
            String reflogOutput = MyApp.checkReflogForCommit("moving from main to v1.0");

            // Check if there's a commit with the message "Added version info for v1.0" and fetch its commit hash
            String commitHashForMessage = MyApp.getCommitHashForMessage("Added version info for v1.0");

            // Check if there's a commit stating "moving from <commitHashForMessage> to main"
            String reflogForHashMove = MyApp.checkReflogForCommit("moving from " + commitHashForMessage + " to main");

            // Check if README.md contains "Version 1.0: Initial release with task creation feature"
            boolean isReadmeContentValid = MyApp.isReadmeContentValid();

            // Assert the validity of all checks
            yakshaAssert(currentTest(), 
                reflogOutput.equals("true") && 
                commitHashForMessage != null && 
                reflogForHashMove.equals("true") && 
                isReadmeContentValid, 
                businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }
    }
}

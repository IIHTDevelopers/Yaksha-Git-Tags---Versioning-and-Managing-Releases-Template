package mainapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MyApp {

    // Method to check if tags v1.0 and v1.1 exist
    public static String areTagsPresent() {
        try {
            System.out.println("Checking if tags 'v1.0' and 'v1.1' exist...");
            String tags = executeCommand("git tag").trim();

            if (tags.contains("v1.0") && tags.contains("v1.1")) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in areTagsPresent method: " + e.getMessage());
            return "";
        }
    }

    // Method to check the commit message for a tag
    public static String checkTagCommitMessage(String tagName, String expectedMessage) {
        try {
            System.out.println("Checking commit message for tag '" + tagName + "'...");
            String tagCommit = executeCommand("git show " + tagName).trim();

            if (tagCommit.contains(expectedMessage)) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in checkTagCommitMessage method: " + e.getMessage());
            return "";
        }
    }

    // Method to fetch commit hashes for a given tag using 'git fsck' and verify if the commit exists in 'git show'
    public static String getCommitHashesForTag(String tagName) {
        try {
            System.out.println("Fetching commit hashes for tag '" + tagName + "'...");
            
            // Execute 'git fsck' to list all tags and their associated commit hashes
            String command = "git fsck --tags --root --unreachable";
            String fsckOutput = executeCommand(command).trim();

            // Search for the tag name and collect commit hashes related to that tag
            StringBuilder commitHashes = new StringBuilder();
            String[] fsckLines = fsckOutput.split("\n");

            for (String line : fsckLines) {
                if (line.contains(tagName)) {
                    String commitHash = line.split(" ")[2].trim(); // Get the commit hash (3rd word in the line)
                    commitHashes.append(commitHash).append("\n");
                }
            }

            // If no commit hashes are found, return an empty string
            if (commitHashes.length() == 0) {
                System.out.println("No commit hashes found for tag '" + tagName + "'");
                return "";
            }

            System.out.println("Commit hashes associated with tag '" + tagName + "':\n" + commitHashes);

            // Now check if the commit hashes are present in the 'git show' output
            String tagShowOutput = executeCommand("git show " + tagName).trim();
            boolean isCommitFound = false;

            // Loop through each commit hash and check if it's present in the 'git show' output
            String[] hashes = commitHashes.toString().split("\n");
            for (String hash : hashes) {
                if (tagShowOutput.contains(hash)) {
                    isCommitFound = true;
                    break;
                }
            }

            // If the commit hashes are found in the 'git show' output, return true
            if (isCommitFound) {
                System.out.println("The commit hashes are present in the 'git show' output for tag '" + tagName + "'");
            } else {
                System.out.println("The commit hashes are NOT found in the 'git show' output for tag '" + tagName + "'");
            }

            // Execute 'git log --oneline' to fetch the commit hash of the specific commit with message "Added list_tasks feature"
            String logOutput = executeCommand("git log --oneline").trim();
            String[] logLines = logOutput.split("\n");

            String commitHashWithMessage = null;

            // Loop through the log and find the commit hash of the commit with message "Added list_tasks feature"
            for (String line : logLines) {
                if (line.contains("Added list_tasks feature")) {
                    commitHashWithMessage = line.split(" ")[0].trim(); // Get the commit hash (first word in the line)
                    break;
                }
            }

            if (commitHashWithMessage != null) {
                System.out.println("Found commit hash for 'Added list_tasks feature': " + commitHashWithMessage);
                
                // Check if this commit hash is present in the 'commitHashes' variable
                if (commitHashes.toString().contains(commitHashWithMessage)) {
                    System.out.println("The commit hash for 'Added list_tasks feature' is found in commitHashes.");
                    return "true";
                } else {
                    System.out.println("The commit hash for 'Added list_tasks feature' is NOT found in commitHashes.");
                    return "false";
                }
            } else {
                System.out.println("Commit with message 'Added list_tasks feature' not found in the git log.");
                return "false";
            }

        } catch (Exception e) {
            System.out.println("Error in getCommitHashesForTag method: " + e.getMessage());
            return "";
        }
    }

    // Method to check if a specific commit exists in the reflog
    public static String checkReflogForCommit(String searchTerm) {
        try {
            System.out.println("Checking reflog for commit: '" + searchTerm + "'...");
            String reflog = executeCommand("git reflog").trim();

            if (reflog.contains(searchTerm)) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            System.out.println("Error in checkReflogForCommit method: " + e.getMessage());
            return "";
        }
    }

    // Method to fetch commit hash for a specific commit message
    public static String getCommitHashForMessage(String commitMessage) {
        try {
            System.out.println("Fetching commit hash for message: '" + commitMessage + "'...");
            String logOutput = executeCommand("git reflog --oneline").trim();
            String[] logLines = logOutput.split("\n");

            for (String line : logLines) {
                if (line.contains(commitMessage)) {
                    // Get the shortened commit hash (first part of the line)
                    String shortCommitHash = line.split(" ")[0].trim();
                    
                    // Resolve the full commit hash using 'git rev-parse'
                    String fullCommitHash = executeCommand("git rev-parse " + shortCommitHash).trim();
                    
                    // Return the full commit hash
                    return fullCommitHash;
                }
            }

            return null; // Return null if commit is not found
        } catch (Exception e) {
            System.out.println("Error in getCommitHashForMessage method: " + e.getMessage());
            return null;
        }
    }

    // Method to check the validity of content in README.md
    public static boolean isReadmeContentValid() {
        try {
            System.out.println("Checking if README.md contains 'Version 1.0: Initial release with task creation feature'...");
            String readmeContent = executeCommand("cat README.md").trim();
            return !readmeContent.contains("Version 1.0: Initial release with task creation feature");
        } catch (Exception e) {
            System.out.println("Error in isReadmeContentValid method: " + e.getMessage());
            return false;
        }
    }

    // Helper method to execute git commands
    private static String executeCommand(String command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(".")); // Ensure this is the correct directory where Git repo is located
        processBuilder.command("bash", "-c", command);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            return output.toString();
        } else {
            System.out.println("Command failed with exit code: " + exitVal);
            throw new RuntimeException("Failed to execute command: " + command);
        }
    }

    // Main method to run the checks manually (can be used to test individually)
    public static void main(String[] args) {
        try {
            // Checking if both tags v1.0 and v1.1 exist
            String tagsExist = areTagsPresent();
            if (tagsExist.equals("true")) {
                System.out.println("Both v1.0 and v1.1 tags exist.");
            } else {
                System.out.println("One or both tags do not exist.");
            }

            // Check commit message for v1.0
            String v1_0CommitMessage = checkTagCommitMessage("v1.0", "v1.0: First stable release of TaskManager");
            System.out.println("v1.0 commit message check: " + v1_0CommitMessage);

            // Check commit message for v1.1
            String v1_1CommitMessage = checkTagCommitMessage("v1.1", "v1.1: Bug fix in create_task function");
            System.out.println("v1.1 commit message check: " + v1_1CommitMessage);

        } catch (Exception e) {
            System.out.println("Error in main method: " + e.getMessage());
        }
    }
}

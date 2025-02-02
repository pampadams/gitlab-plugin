package com.dabsquared.gitlabjenkins.service;

import static com.dabsquared.gitlabjenkins.gitlab.api.model.builder.generated.BranchBuilder.branch;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import com.dabsquared.gitlabjenkins.gitlab.api.model.Branch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GitLabProjectBranchesServiceTest {
    private final static List<String> BRANCH_NAMES_PROJECT_B = Arrays.asList("master", "B-branch-1", "B-branch-2");

    private GitLabProjectBranchesService branchesService;

    private GitLabClientStub clientStub;

    @Before
    public void setUp() throws IOException {
        clientStub = new GitLabClientStub();
        clientStub.addBranches("groupOne/A", convert(Arrays.asList("master", "A-branch-1")));
        clientStub.addBranches("groupOne/B", convert(BRANCH_NAMES_PROJECT_B));


        // never expire cache for tests
        branchesService = new GitLabProjectBranchesService();
    }

    @Test
    public void shouldReturnBranchNamesFromGitlabApi() {
        // when
        List<String> actualBranchNames = branchesService.getBranches(clientStub, "git@git.example.com:groupOne/B.git");

        // then
        assertThat(actualBranchNames, is(BRANCH_NAMES_PROJECT_B));
    }

    @Test
    public void shouldNotMakeUnnecessaryCallsToGitlabApiGetBranches() {
        // when
        branchesService.getBranches(clientStub, "git@git.example.com:groupOne/A.git");

        // then
        assertEquals(1, clientStub.calls("groupOne/A", Branch.class));
        assertEquals(0, clientStub.calls("groupOne/B", Branch.class));
    }

    private List<Branch> convert(List<String> branchNames) {
        ArrayList<Branch> result = new ArrayList<>();
        for (String branchName : branchNames) {
            result.add(branch().withName(branchName).build());
        }
        return result;
    }
}

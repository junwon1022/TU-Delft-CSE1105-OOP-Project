# Code Contributions and Code Reviews


#### Focused Commits

Grade: Good

Feedback: Good amount of commits that affect a small number of files. Try to aggregate commits that are very similar and avoid commiting before you are sure e.g. changing the backlog (draft, final etc), no need for 6 commits, you can do it in one that changes 2-3 files and maybe add a description of what it does. Good titles in the commits, not too long, not too short, keep it like that :)


#### Isolation

Grade: Good

Feedback: Since this week didn't have a lot of code writing it is a bit early to give feedback on this, but so far it seems that the branches/merge requests do isolate individual problems/features/uploadings. I believe there was one MR with 15 commits that had only a single change which seems a bit excessive, try to generally avoid this in the future. I did see a commit directly to the main branch - it is good to also avoid this in general.


#### Reviewability

Grade: Good

Feedback: Again since this week didn't have a lot of code writing but based on the current repo status here is some feedback. Generally your MRs have a clear focus, contain a low number of formatting changes and only cover a small number of commits. I did see one exception where the name didn't quite match the description "Dev into main" and something else was the description. Generally try to have the description of what the MR does in the title (but of course reasonably short) and a better more detailed description in the description box underneath. Avoid titles that say which branches are merged as this is already shown on gitlab, unless of course it is your weekly sprint update from Dev->Main and that is what the MR does. MR branches don't stay open for too long before merging and no current merge conflicts. So the process is pretty good so far!


#### Code Reviews

Grade: Sufficient

Feedback: I really like that you used gitlab features of "Assignees", I recommend using the rest as well e.g. "Reviewers" etc. I noticed you haven't started writing comments in the MRs yet. In the future, it is good to write comments before approving an MR. Also, when there is a minimum of two people approving the MR then two people _other_ than the person making the MR need to approve it - unless of course you have decided as a team that you will need only one approval to do MRs.


#### Build Server

Grade: Sufficient

Feedback: Builds (pipelines) pass so far almost every time which is great, but that is because it is only set up to run the build. Make sure you select 10+ checkstyle rules and add them to your checkstyle.xml (check that the pipeline on gitlab also runs it).


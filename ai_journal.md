## Entry 1: Modulith spec and module selection

### Prompt
Create a spec file for Modulith application which supports these requirments

- Claimants need to be able to report an incident, track their claim, provide additional information when asked, and receive decisions.
- Claims staff need to be able to pick up incoming claims, review and assess them, progress claims to settlement or rejection, and see their team's workload and performance.

### Response
4 modules: Claims, Assignments, Workflow, Management

### Decision
Partially Accepted

### Reasoning
As assignment of claims is part of claims, rejected Assignments. Use Claims, Workflow.

## Entry 2: Generate Claim module from specs

### Prompt
From the spec file create the Claims module from controller to repository and define the entities for claims. Make sure it doesn't depend on other modules.

### Response
Generated all module folder and created controller in seperate folder path names api/ and created folders with infrastructure, application,and doiman

### Decision
Not Accepted

### Reasoning
As this is not the standard spring application folder structure

## Entry 3: Refactor folder names and paths

### Prompt
Instead of using infrastructure, Application and domain. Create the folder structure with Controller Service Repository Entity for all modules and adjust the imports from the generated Source.

### Response
Refactored and Generated all folder in claims modules and changed for all other modules

### Decision
Accepted

## Entry 4: refactor service logic

### Prompt
Change the service logic for the dashboard response by adding a case query instead of counting from stream of findall response

### Response
create 3 seperate queries for all to get pending, approved and rejected
    long countByStatus(ClaimStatus status);

    long countByStatusIn(Collection<ClaimStatus> statuses);

    @Query("select coalesce(sum(c.estimatedAmount), 0) from Claim c where c.status not in :excludedStatuses")
    double sumEstimatedAmountByStatusNotIn(@Param("excludedStatuses") Collection<ClaimStatus> excludedStatuses);


### Decision
Not Accepted

### Reasoning
As this call interacts 3 times with database everytime to get the stats it is overhead, and we can use projection to get all at once with case

## Entry 5: case logic

### Prompt
As this makes 3 seperate query interactions with database, use the case with new projection to fecth all data in single query

### Response
findDashboardStats method with new projection Dashboardstats

### Decision
Accepted

## Entry 6: unit test cases
### Prompt
write the unit test cases for the claims module by following the folder structure.

### Response
Generated test cases for all files including mappers
### Decision
Accepted and removed the mapper tests as it is unnecessary to test for conversions without logic.

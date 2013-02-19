# To run this application following softwares will be required:
	1. apache-maven-2.2.1
	2. JBoss AS 5.1
	3. Oracle 11g
	
# Deployment instruction
	1. Build the application using maven. Go into the application base folder "/oauth-seam-app" and run "mvn clean install"
	2. A ear file will get generated in the location - "/oauth-seam-app/ear-module/target". Place the ear file in the JBoss at the location "%JBOSS_HOME%/server/default/farm"
	3. Run the scripts in the folder "oauth-seam-app/ejb-module/src/main/sql" in any of your Oracle instance
	4. Change the DB details in the file "oauth-seam-app/ejb-module/src/main/config/oauthSeam-ds.xml" and place it in the JBoss folder "%JBOSS_HOME%/server/default/deploy"
	5. Start the JBoss server
	6. Run the client code from attached oauth-seam-client
	
# OAuth specific configurations
	This project contains lots of configuration details as this had to be built for standalone running capable. This may be annoying for the users who will
	be using the OAuth-Resteasy part for their existing project. For them below provided the OAuth specific configuration details: 
	
	A] Maven dependencies
		Check dependencies placed under the comment "<!-- Dependency for JBOSS RestEasy -->" in the pom xml file in ejb-module project
	
	B] For Tables required specific to OAuth implementation, refer to oauth.sql file
	
	C] web.xml - multiple changes are required in this place:
		1. Add the context parameters
			a) oauth.provider.provider-class (OAuth provider class implementation)
			b) oauth.provider.tokens.request (for specifying request-token URL)
			c) oauth.provider.tokens.access (for specifying access-request URL)
			
		2. Configure OAuth Filter
		
		3. Configure and Map OAuth servlet.
		
	D] component.xml - configure REST service URL base path: 
		<resteasy:application resource-path-prefix="/oauth-seam-rest"/>
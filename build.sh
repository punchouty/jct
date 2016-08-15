mv admin.war admin_bak.war
mv user.war user_bak.war
cp /Users/rajan/Documents/dev/workspace/jct/JCT/target/user.war .
cp /Users/rajan/Documents/dev/workspace/jct/JCTAdmin/target/admin.war .
jar cvf app_11.zip admin.war user.war ROOT.war

from django.db import models
 
# Create your models here.
 
class User(models.Model):
    user_id = models.CharField(max_length=128, null=False)
    password = models.CharField(max_length=128, null=False)
    name = models.CharField(max_length=128, null=True)
    point = models.IntegerField(null=True)
    token = models.CharField(max_length=256, null=True)
    rtoken = models.CharField(max_length=256, null=True)
    lastcheck = models.IntegerField(null=True)
 
    class Meta:
        db_table = "User"

class SecureUser(models.Model):
	user_id = models.CharField(max_length=128, null=False)
	point = models.IntegerField(null=False)
	name = models.CharField(max_length=128, null=True)
	
	class Meta:
		db_table = "SecureUser"

class GPS(models.Model):
	address = models.CharField(max_length=128, null=False)
	latitude = models.CharField(max_length=128, null=False)
	longitude = models.CharField(max_length=128, null=False)

	class Meta:
		db_table = "GPS"
        
class Follow(models.Model):
	follower = models.CharField(max_length=128, null=False)
	followed = models.CharField(max_length=128, null=False)
	
	class Meta:
		db_table = "Follow"

from rest_framework import serializers
from .models import User, SecureUser, Follow, GPS
 
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'

class SecureUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = SecureUser
        fields = '__all__'

class GPSSerializer(serializers.ModelSerializer):
    class Meta:
        model = GPS
        fields = '__all__'
        
class FollowSerializer(serializers.ModelSerializer):
    class Meta:
        model = Follow
        fields = '__all__'

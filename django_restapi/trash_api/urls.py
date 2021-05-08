from django.urls import path
from . import views
 
app_name = 'trash_api'

urlpatterns = [
    path('signup', views.SignupAPI.as_view()),
    path('login', views.LoginAPI.as_view()),
    path('logout', views.LogoutAPI.as_view()),
    path('users/users', views.UserAPI.as_view()),
    path('users/updatepw', views.UpdatepwAPI.as_view()),
    path('users/follow', views.FollowAPI.as_view()),
    path('users/point', views.PointAPI.as_view()),
    path('gps', views.GPSAPI.as_view()),
    path('refresh', views.RefreshAPI.as_view()),
]


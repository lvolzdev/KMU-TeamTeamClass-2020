from django.contrib import admin
from django.urls import path
from django.conf.urls import include
 
urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('trash_api.urls'), name='trash_api'), 
]

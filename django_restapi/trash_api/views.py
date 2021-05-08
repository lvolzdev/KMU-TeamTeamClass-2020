from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import UserSerializer, SecureUserSerializer, FollowSerializer, GPSSerializer
from rest_framework import status
from .models import User, SecureUser, Follow, GPS
from django.http import HttpResponse
from datetime import datetime, timedelta
import jwt, collections

SECRET_KEY = 'dGxyaGRkbWx2aHJ2bmRkbXN3amRha2ZjaGxyaGRp'
R_SECRET_KEY = 'Z2t0bXRteGhzcmt4ZG1zYWtkcnBhZG1mc25ya2drc2k='

class SignupAPI(APIView):
	
	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
	
	def post(self, request): # 회원가입을 하는 함수
		
		user_id = request.data['user_id']
		password = request.data['password']
		name = request.data['name']
		
		nowdate = datetime.today().year*10000 + datetime.today().month*100 + datetime.today().day
		
		try:
			user_object = User.objects.get(user_id=user_id)
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 중복된 아이디가 존재
		except: 
			userdict = {'user_id': user_id, 'password': password, 'point': 0, 'token': 'null', 'rtoken': 'null', 'name': name, 'lastcheck': nowdate-1}
			s_userdict = {'user_id': user_id, 'point': 0, 'name': name}
		
			user_serializer = UserSerializer(data=userdict)
			secure_user_serializer = SecureUserSerializer(data=s_userdict)
		
			if user_serializer.is_valid() and secure_user_serializer.is_valid():
				user_serializer.save()
				secure_user_serializer.save()
				return HttpResponse('signup success', status=status.HTTP_201_CREATED) # 회원가입 완료
			else:
				return HttpResponse('error2', status=status.HTTP_400_BAD_REQUEST) # 회원가입 실패
	
class LoginAPI(APIView):

	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)

	def put(self, request): # 로그인을 하는 함수
		
		user_id = request.data['user_id']
		password = request.data['password']
		
		try:
			user_object = User.objects.get(user_id=user_id)
		except:
			return HttpResponse('error1', status=status.HTTP_400_BAD_REQUEST) # 해당하는 아이디가 없음
		
		user_serializer = UserSerializer(User.objects.get(user_id=user_id))
		
		if user_serializer.data['password'] == password:
			
			token = jwt.encode({'user_id':user_id, 'exp':datetime.utcnow()+timedelta(minutes=30)}, SECRET_KEY, algorithm='HS256')
			rtoken = jwt.encode({'user_id':user_id, 'exp':datetime.utcnow()+timedelta(days=1)}, R_SECRET_KEY, algorithm='HS256')
			token = str(token)[2:len(str(token))-1]
			rtoken = str(rtoken)[2:len(str(rtoken))-1]
			print(token)
			print(rtoken)
			point = user_serializer.data['point']
			name = user_serializer.data['name']
			lastcheck = user_serializer.data['lastcheck']
			
			userdict = {'user_id': user_id, 'password': password, 'point': point, 'token': token, 'rtoken': rtoken, 'name': name, 'lastcheck': lastcheck}
			update_user_serializer = UserSerializer(user_object, data=userdict)
			
			order = str()
			dic = {"token": token, "rtoken": rtoken}
			order = order + str(collections.OrderedDict(dic))
			
			if update_user_serializer.is_valid():
				update_user_serializer.save()
				return HttpResponse(order, status=status.HTTP_200_OK) # 로그인 성공, 2개의 토큰을 모두 넘겨야함
		
		return HttpResponse('error2', status=status.HTTP_401_UNAUTHORIZED) # 패스워드 미일치로 로그인 실패

class RefreshAPI(APIView):
	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
	
	def put(self, request):
		input_token = request.data['token']
		input_rtoken = request.data['rtoken']
		if(input_token[0]=='\''): input_token = input_token[1:len(input_token)-1]
		if(input_rtoken[0]=='\''): input_rtoken = input_rtoken[1:len(input_rtoken)-1]
		
		try:
			jwt.decode(bytes(input_token.encode('utf-8')), SECRET_KEY, algorithms='HS256')
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED)
		except:
			pass
		
		try:
			jwt.decode(bytes(input_rtoken.encode('utf-8')), R_SECRET_KEY, algorithms='HS256')
		except jwt.ExpiredSignatureError:
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 유효기간 만료
		except jwt.InvalidTokenError:
			return HttpResponse('error2', status=status.HTTP_401_UNAUTHORIZED) # 아무튼 잘못됨 뭐가 잘못된건진 모르겠는데 아무튼 잘못됨
		
		try:
			user_object = User.objects.get(rtoken=input_rtoken)
		except:
			return HttpResponse('error3', status=status.HTTP_401_UNAUTHORIZED) # 해당하는 토큰이 없음
		
		user_serializer = UserSerializer(user_object)
		
		user_id = user_serializer.data['user_id']
		
		token = jwt.encode({'user_id':user_id, 'exp':datetime.utcnow()+timedelta(minutes=30)}, SECRET_KEY, algorithm='HS256')
		rtoken = jwt.encode({'user_id':user_id, 'exp':datetime.utcnow()+timedelta(days=1)}, R_SECRET_KEY, algorithm='HS256')
		token = str(token)[2:len(str(token))-1]
		rtoken = str(rtoken)[2:len(str(rtoken))-1]
		print(token)
		print(rtoken)
		password = user_serializer.data['password']
		point = user_serializer.data['point']
		name = user_serializer.data['name']
		lastcheck = user_serializer.data['lastcheck']
		
		userdict = {'user_id': user_id, 'password': password, 'point': point, 'token': token, 'rtoken': rtoken, 'name': name, 'lastcheck': lastcheck}
		update_user_serializer = UserSerializer(user_object, data=userdict)
		
		order = str()
		dic = {"token": token, "rtoken": rtoken}
		order = order + str(collections.OrderedDict(dic))
		
		if update_user_serializer.is_valid():
			update_user_serializer.save()
			return HttpResponse(order, status=status.HTTP_200_OK) # refresh 성공
		return HttpResponse('error4', status=status.HTTP_401_UNAUTHORIZED) # refresh 실패

class LogoutAPI(APIView):
	
	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
	
	def put(self, request): # 로그아웃을 하는 함수
	
		token = request.data['token']
		if(token[0]=='\''): token = token[1:len(token)-1]
		try:
			user_object = User.objects.get(token=token)
		except:
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 토큰 찾기 실패
		
		user_serializer = UserSerializer(user_object)
		
		user_id = user_serializer.data['user_id']
		password = user_serializer.data['password']
		point = user_serializer.data['point']
		name = user_serializer.data['name']
		lastcheck = user_serializer.data['lastcheck']
		
		userdict = {'user_id': user_id, 'password': password, 'point': point, 'token': 'null', 'rtoken': 'null', 'name': name, 'lastcheck': lastcheck}
		update_user_serializer = UserSerializer(user_object, data=userdict)
		if update_user_serializer.is_valid():
			update_user_serializer.save()
			return HttpResponse('logout success', status=status.HTTP_200_OK)
		return HttpResponse('error2', status=status.HTTP_400_BAD_REQUEST)
			
class UserAPI(APIView):

	def get(self, request):
		return Response("invalid request - direct connection", status=status.HTTP_400_BAD_REQUEST)

	def put(self, request):
		
		token = request.data['token']
		where = request.data['where']
		if(token[0]=='\''): token = token[1:len(token)-1]
		
		try:
			jwt.decode(bytes(token.encode('utf-8')), SECRET_KEY, algorithms='HS256')
		except jwt.ExpiredSignatureError:
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 유효기간 만료
		except jwt.InvalidTokenError:
			return HttpResponse('error2', status=status.HTTP_401_UNAUTHORIZED) # 아무튼 잘못됨 뭐가 잘못된건진 모르겠는데 아무튼 잘못됨
		
		try:
			user_object = User.objects.get(token=token)
		except:
			return HttpResponse("error3", status=status.HTTP_401_UNAUTHORIZED) # 토큰 찾기 실패
		
		user_serializer = UserSerializer(user_object)
		
		if(where == 'all'):
			user_queryset = SecureUser.objects.all()
			user_queryset_serializer = SecureUserSerializer(user_queryset, many=True)
			print(user_queryset_serializer.data[0])
			order = ""
			for i in range(len(user_queryset_serializer.data)):
				find = user_queryset_serializer.data[i]
				dic = {'user_id': find['user_id'], 'point': find['point']}
				order = order + str(collections.OrderedDict(dic))
			return HttpResponse(order, status=status.HTTP_200_OK) # 성공
		elif(where == 'me'):
			print(user_serializer.data)
			dic = {'user_id': user_serializer.data['user_id'], 'name': user_serializer.data['name'], 'point': user_serializer.data['point']}
			print(dic)
			return HttpResponse(str(collections.OrderedDict(dic)), status=status.HTTP_200_OK) # 성공
			
		else:
			return HttpResponse("error4", status=status.HTTP_400_BAD_REQUEST) # where을 잘못씀
	
	# 여기 수정 해야겠다
	def delete(self, request, **kwargs): # User 정보를 지우는 함수
		#token = request.data['token']
		alldelete = True # 모두 지울지 여부 확인
		
		try:
			#user_object = User.objects.get(token=token)
			pass
		except:
			return HttpResponse("delete failed - no token", status=status.HTTP_400_BAD_REQUEST) # 토큰 찾기 실패
		
		#user_serializer = UserSerializer(user_object)

		if(alldelete): # 모두 지움
			user_queryset = User.objects.all() 
			user_queryset.delete()
			suser_queryset = SecureUser.objects.all()
			suser_queryset.delete()
			follow_queryset = Follow.objects.all()
			follow_queryset.delete()
			return HttpResponse("delete all User complete", status=status.HTTP_200_OK)

		user_id = user_serializer.data['user_id']

		user_object = SecureUser.objects.get(user_id=user_id)
		user_object.delete()
		return HttpResponse("delete complete", status=status.HTTP_200_OK)

class PointAPI(APIView):
	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
	
	def put(self, request): # 포인트를 추가하는 함수
		
		token = request.data['token']
		if(token[0]=='\''): token = token[1:len(token)-1]
		print(token)
		
		try:
			jwt.decode(bytes(token.encode('utf-8')), SECRET_KEY, algorithms='HS256')
		except jwt.ExpiredSignatureError:
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 유효기간 만료
		except jwt.InvalidTokenError:
			return HttpResponse('error2', status=status.HTTP_401_UNAUTHORIZED) # 아무튼 잘못됨 뭐가 잘못된건진 모르겠는데 아무튼 잘못됨
		
		try:
			user_object = User.objects.get(token=token)
		except:
			return HttpResponse("error3", status=status.HTTP_401_UNAUTHORIZED) # 토큰 찾기 실패
			
		user_serializer = UserSerializer(user_object)
		
		nowdate = datetime.today().year*10000 + datetime.today().month*100 + datetime.today().day
		
		if(nowdate <= user_serializer.data['lastcheck']):
			return HttpResponse('date error', status=status.HTTP_200_OK)
		
		userdict = {'user_id': user_serializer.data['user_id'], 'password': user_serializer.data['password'], 'point': user_serializer.data['point'] + 1, 'token': token, 'rtoken': user_serializer.data['rtoken'], 'name': user_serializer.data['name'], 'lastcheck': nowdate}
		suserdict = {'user_id': user_serializer.data['user_id'], 'point': user_serializer.data['point'] + 1, 'name': user_serializer.data['name']}
		
		suser_object = SecureUser.objects.get(user_id=user_serializer.data['user_id'])
		
		update_user_serializer = UserSerializer(user_object, data=userdict)
		update_suser_serializer = SecureUserSerializer(suser_object, data=suserdict)
		
		if update_user_serializer.is_valid() and update_suser_serializer.is_valid():
			update_user_serializer.save()
			update_suser_serializer.save()
			return HttpResponse("point change success", status=status.HTTP_200_OK)
		else:
			return HttpResponse("error5", status=status.HTTP_400_BAD_REQUEST)

class GPSAPI(APIView):
	
	def get(self, request):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)
	
	def put(self, request):
	
		token = request.data['token']
		latitude = request.data['latitude']
		longitude = request.data['longitude']
		if(token[0]=='\''): token = token[1:len(token)-1]
		
		try:
			jwt.decode(bytes(token.encode('utf-8')), SECRET_KEY, algorithms='HS256')
			print(token)
		except jwt.ExpiredSignatureError:
			return HttpResponse('error1', status=status.HTTP_401_UNAUTHORIZED) # 유효기간 만료
		except jwt.InvalidTokenError:
			return HttpResponse('error2', status=status.HTTP_401_UNAUTHORIZED) # 아무튼 잘못됨 뭐가 잘못된건진 모르겠는데 아무튼 잘못됨
			
		try:
			user_object = User.objects.get(token=token)
		except:
			return HttpResponse("error3", status=status.HTTP_401_UNAUTHORIZED) # 토큰 찾기 실패
		
		gps_queryset = GPS.objects.all()
		gps_queryset_serializer = GPSSerializer(gps_queryset, many=True)
		gpsdic = gps_queryset_serializer.data

		arr = list()
		for dic in gpsdic:
			arr.append([(float(dic['latitude'])-float(latitude))**2 + (float(dic['longitude'])-float(longitude))**2, dic['address'], dic['latitude'], dic['longitude']])
		arr = sorted(arr)[:3]
		
		order = str()
		for find in arr:
			dic = {"address": find[1], "latitude": find[2], "longitude": find[3]}
			dic['address'] = dic['address'].replace('(', '')
			dic['address'] = dic['address'].replace(')', '')
			order = order + str(collections.OrderedDict(dic))
		print(order)
		
		return HttpResponse(order, status=status.HTTP_200_OK)
	
	def post(self, request):
		f1 = open("gpsdata1.txt", 'r')
		f2 = open("gpsdata2.txt", 'r')
		count=0
		while True:
			line1 = f1.readline()
			line2 = f2.readline()
			arr1 = line1.split('\t')
			arr2 = line2.split('\t')
			if not line1: break
			if(arr1[4]=='정좌표'):
				address = arr1[1]
				latitude = arr1[3]
				longitude = arr1[2]
				
				gpsdict = {'address': address, 'latitude': latitude, 'longitude': longitude}
				gps_serializer = GPSSerializer(data=gpsdict)
				
				if gps_serializer.is_valid():
					gps_serializer.save()
					count+=1
				
			elif(arr2[4]=='정좌표'):
				address = arr2[1]
				latitude = arr2[3]
				longitude = arr2[2]
		
				gpsdict = {'address': address, 'latitude': latitude, 'longitude': longitude}
				gps_serializer = GPSSerializer(data=gpsdict)
				
				if gps_serializer.is_valid():
					gps_serializer.save()
					count+=1
		print(count)
		return HttpResponse("data input success", status=status.HTTP_201_CREATED)
	
	def delete(self, request):
		gps_queryset = GPS.objects.all() 
		gps_queryset.delete()
		return HttpResponse('gps all delete success', status=status.HTTP_200_OK)
		

########### dummy code

class UpdatepwAPI(APIView):

	def get(self, request,  **kwargs):
		return Response("invalid request", status=status.HTTP_400_BAD_REQUEST)

	def put(self, request, **kwargs): # 비밀번호를 변경하는 함수
	
		token = request.data['token']
		oldpassword = request.data['oldpassword']
		newpassword = request.data['newpassword']
		
		try:
			user_object = User.objects.get(token=token)
		except:
			return HttpResponse("invalid request - no token", status=status.HTTP_400_BAD_REQUEST) # 토큰 찾기 실패
		user_serializer = UserSerializer(user_object)
        
		user_id = user_serializer.data['user_id']
		password = user_serializer.data['password']
		point = user_serializer.data['point']
		name = user_serializer.data['name']
		userdict = {'user_id': user_id, 'password': newpassword, 'point': point, 'token': token, 'name': name}

		if oldpassword != password:
			return HttpResponse("invalid request - no oldpassword", status=status.HTTP_400_BAD_REQUEST) # 패스워드가 일치하지 않음
			
		update_user_serializer = UserSerializer(user_object, data=userdict)
		if update_user_serializer.is_valid():
			update_user_serializer.save()
			return HttpResponse("change success", status=status.HTTP_200_OK)
		else:
			return HttpResponse("invalid request", status=status.HTTP_400_BAD_REQUEST)

		
class FollowAPI(APIView):
	def post(self, request): # follow정보를 추가하는 함수

		token = request.data['token']
		followed = request.data['followed']

		try:
			user_object = User.objects.get(token=token)
		except:
			return Response("invalid request - no token", status=status.HTTP_400_BAD_REQUEST) # 토큰 찾기 실패
			
		try:
			tmp_object = User.objects.get(user_id=followed)
		except:
			return Response("invalid request - no followed", status=status.HTTP_400_BAD_REQUEST) # 사람이 없음

		user_serializer = UserSerializer(user_object)

		try:
			user_object = User.objects.get(user_id=user_id)
			return Response('signup failed - same follow', status=status.HTTP_400_BAD_REQUEST) # 중복된 팔로우가 존재
		except: 
			followdict = {'follower': user_serializer.data['user_id'], 'followed': followed}
			follow_serializer = FollowSerializer(data=followdict)

			if follow_serializer.is_valid():
				follow_serializer.save()
				print(follow_serializer.data)
				return Response('follow success', status=status.HTTP_201_CREATED)
			else:
				return Response('follow failed', status=status.HTTP_400_BAD_REQUEST)
 
	def get(self, request,  **kwargs): # 자신이 follower인 모든 정보를 가져오는 함수

		token = request.data['token']

		try:
			user_object = User.objects.get(token=token)
		except:
			return Response("invalid request - no token", status=status.HTTP_400_BAD_REQUEST) # 토큰 찾기 실패

		user_serializer = UserSerializer(user_object)

		follower = user_serializer.data['user_id']
		print(follower)
		
		try:
			follow_object = Follow.objects.filter(follower=follower)
		except:
			return Response("invalid request - no follow", status=status.HTTP_400_BAD_REQUEST) # 링크 찾기 실패
		follow_serializer = FollowSerializer(follow_object, many=True)

		# 여기서 follower들을 user 배열 json 으로 바꾸기
		# 클라이언트 쪽에서 반복문을 하나만 더 추가해주면 사실 안해도 되긴 함

		return Response(follow_serializer.data, status=status.HTTP_201_CREATED)
 
	def delete(self, request, **kwargs): # 자신이 follower인 정보 하나를 지우는 함수
		token = request.data['token']
		followed = request.data['followed']

		try:
			user_object = User.objects.get(token=token)
		except:
			return Response("invalid request - no token", status=status.HTTP_400_BAD_REQUEST) # 토큰 찾기 실패

		user_serializer = UserSerializer(user_object)
		follower = user_serializer.data['user_id']

		try:
			follow_object = Follow.objects.get(follower=follower) # followed도 확인
		except:
			return Response("invalid request - no follow", status=status.HTTP_400_BAD_REQUEST) # 팔로우 관계가 없음

		follow_object.delete()
		return Response("delete complete", status=status.HTTP_200_OK)

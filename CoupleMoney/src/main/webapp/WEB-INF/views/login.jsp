<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hello World!!</title>
<link rel="stylesheet" href="<c:url value='/webjars/bootstrap/3.3.4/dist/css/bootstrap.min.css'/>">
</head>
<body role="document">
	<div class="container theme-showcase" role="main">
		<p>
			<button type="button" class="btn btn-lg btn-default" onClick="getAuthorizationUrl('daum')">DAUM</button>
			<button type="button" class="btn btn-lg btn-primary" onClick="getAuthorizationUrl('kakao')">KAKAO</button>
			<button type="button" class="btn btn-lg btn-success" onClick="getAuthorizationUrl('naver')">NAVER</button>
			<button type="button" class="btn btn-lg btn-info" onClick="getAuthorizationUrl('google')">GOOGLE</button>
			<button type="button" class="btn btn-lg btn-warning" onClick="getAuthorizationUrl('twitter')">TWITTER</button>
			<button type="button" class="btn btn-lg btn-danger">Danger</button>
			<button type="button" class="btn btn-lg btn-link">Link</button>
		</p>
	</div> <!-- /container -->
	
	<script src="<c:url value='/webjars/jquery/2.1.3/dist/jquery.min.js'/>"></script>
	<script src="<c:url value='/webjars/bootstrap/3.3.4/dist/js/bootstrap.min.js'/>"></script>
	<script type="text/javascript">
	function getAuthorizationUrl(socialType) {
		$.ajax({
			url: "<c:url value='/social/oauth/'/>" + socialType,
			type: 'GET',
			dataType: 'json',
			success: function(result) {
				window.location.href = result.url;
			}, fail: function(result) {
				console.log(result);
			}
		});
	}
	</script>
</body>
</html>
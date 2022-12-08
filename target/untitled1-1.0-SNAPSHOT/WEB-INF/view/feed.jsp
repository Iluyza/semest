<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="/js/jquery-3.6.1.min.js"></script>
<html>
<head>
    <title>Feed</title>
</head>
<body>
<h2>My feed</h2>
<c:set var="list" value="${sessionScope.feed}"/>
<c:choose>
    <c:when test="${list!=null}">
        <table style="margin-right: 100px; margin-bottom: 30px; float: left">
            <c:forEach var="Post" items="${list}">
                <tr>
                    <td><c:out value="${Post.owner}"/></td>
                </tr>
                <tr>
                    <td><c:out value="${Post.name}"/></td>
                </tr>
                <tr>
                    <td><c:out value="${Post.text}"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
</c:choose>
</body>
</html>

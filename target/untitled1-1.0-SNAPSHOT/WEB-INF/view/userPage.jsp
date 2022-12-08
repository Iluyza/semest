<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>User page</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.6.1.js"></script>
</head>
<body>
<h1><c:out value="${sessionScope.Username}"/></h1>
<c:set var="list" value="${sessionScope.feed}"/>
<c:set var="follow" value="${sessionScope.toFollow}"/>
<c:choose>
    <c:when test="${follow!=null}">
        <form method="post">
            <button type="submit">${follow}</button>
            <input type="hidden" name="action" value="follow"/>
        </form>
    </c:when>
    <c:when test="${follow==null}">
        <form method="post">
            <button type="submit">Sign off</button>
            <input type="hidden" name="action" value="delete"/>
        </form>
    </c:when>
</c:choose>
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

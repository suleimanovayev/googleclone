<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>Page4</title>
</head>
<body>
SEARCH RESULT
    <c:forEach var="page" items="${pages}">
        <tr>
            <p><a href="${page.url}">${page.url}</a></p>

            <p>${page.title}</p><br/>
        </tr>
    </c:forEach>
</body>
</html>

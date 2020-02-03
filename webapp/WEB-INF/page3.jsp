<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Page3</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/search" method="post">
    <div class="container">
        <label for="text"><b>Text</b></label>
        <input type="text" placeholder="Enter text" name="query" required>

        <button type="submit" class="registerbtn">Search</button>
    </div>
</form>
</body>
</html>

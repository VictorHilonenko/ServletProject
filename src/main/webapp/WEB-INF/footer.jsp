<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

    <input type="hidden" id="error_tryLater" value="<fmt:message key="error.tryLater"/>">
    <input type="hidden" id="success_saved" value="<fmt:message key="success.saved"/>">
    <input type="hidden" id="success_reserved" value="<fmt:message key="success.reserved"/>">

    <br>
    <br>
    <br>

    <script src="/js/toastr.js"></script>
	<script src="/js/common.js"></script>
</body>
</html>
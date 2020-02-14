<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/header.jsp" />

	<section>
		<h1><span><fmt:message key="i18n.appointments" /></span> <span id="headerDates" class="dates">06.01 - 12.01</span></h1>
        <c:if test = "${userPrincipal.hasRoleTag('nonstaff')}">
            <div class="add_new">
                <c:choose>
                    <c:when test = "${userPrincipal.hasRoleTag('authenticated')}">
                        <a href="" class="add_button" onclick="openAppointment(null); return false;"><fmt:message key="i18n.newAppointment" /></a>
                    </c:when>

                    <c:otherwise>
                        <a href="" class="add_button" onclick="suggestRegistration(); return false;"><fmt:message key="i18n.newAppointment" /></a>
                    </c:otherwise>
                </c:choose>
    		</div>
        </c:if>

<!--		<div class="legend"> -->
<!-- 			<input type="checkbox" name="haircut" value="HAIRDRESSING" class="checkbox"> -->
<!-- 			<label for="haircut"> -->
<!-- 				<a href="" class="serviceType appointment_h">Hairdressing</a> -->
<!-- 			</label> -->
<!-- 			<input type="checkbox" name="make-up" value="MAKEUP" class="checkbox"> -->
<!-- 			<label for="make-up"> -->
<!-- 				<a href="" class="serviceType appointment_m">Makeup</a> -->
<!-- 			</label> -->
<!-- 			<input type="checkbox" name="cosmetology" value="COSMETOLOGY" class="checkbox"> -->
<!-- 			<label for="cosmetology"> -->
<!-- 				<a href="" class="serviceType appointment_c">Cosmetology</a> -->
<!-- 			</label> -->
<!-- 		</div> -->
		<div class="schedule_cont">
			<table id="table_schedule" class="table_schedule">
				<tr>
					<th style="width: 7%">
						<a href="" class="arrow_left" onclick = "drawPrevWeek(); return false;"><img src="/images/arrow_left.png" width="17px" border="0" alt="prev week"></a>
						<a href="" onclick = "drawThisWeek(); return false;"><img src="/images/today.png" width="24px" border="0" alt="This week"></a>
						<a href="" class="arrow_right" onclick = "drawNextWeek(); return false;"><img src="/images/arrow_right.png" width="17px" border="0" alt="next week"></a>
					</th>
					<th style="width: 10%"><span><fmt:message key="week.day0"/></span><br><span id="d0" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day1"/></span><br><span id="d1" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day2"/></span><br><span id="d2" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day3"/></span><br><span id="d3" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day4"/></span><br><span id="d4" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day5"/></span><br><span id="d5" class="dates"></span></th>
					<th style="width: 10%"><span><fmt:message key="week.day6"/></span><br><span id="d6" class="dates"></span></th>
				</tr>
				<c:forEach var="hour" begin="${WORK_TIME_STARTS}" end="${WORK_TIME_ENDS}">
					<tr>
						<th>${hour}:00</th>
						<td id="d0_${hour}"></td>
						<td id="d1_${hour}"></td>
						<td id="d2_${hour}"></td>
						<td id="d3_${hour}"></td>
						<td id="d4_${hour}"></td>
						<td id="d5_${hour}"></td>
						<td id="d6_${hour}"></td>
					</tr>
				</c:forEach>				
			</table>
		</div>
    </section>
    <br>
    <br>
    <br>
    
	<div id="dialog-login" title="You have to be logged in" class="dialogs">
		<p>Dear Guest!</p>
		<p>You can make an appointment as soon <br>
		as you are logged in to our BeautyScheduler</p>
		<p>Don't have an account? <br>
		Registration procedure will take less than a minute...</p>
	</div>
    
	<div id="dialog-appointment" title="Appointment" class="dialogs">
		<div id="div_id" class="form-block">
			<span class="popup_label">Id:</span>
			<input type="text" id="id" class="popup_input field">
		</div>
		
		<div id="div_date" class="form-block">
			<span class="popup_label">Date:</span>
			<input type="text" id="date" class="popup_input field">
		</div>
		
		<div id="div_time" class="form-block">
			<span class="popup_label">Time:</span>
			<select id="time" class="popup_select field">
				<c:forEach var="hour" begin="${WORK_TIME_STARTS}" end="${WORK_TIME_ENDS}">
					<option value="${hour}">${hour}:00</option>
				</c:forEach>				
			</select>
		</div>
		
		<div id="div_serviceType" class="form-block">
			<span class="popup_label">Service type:</span>
			<select id="serviceType" class="popup_select field">
				<c:forEach items="${serviceTypes}" var="serviceType">
				    <option value="${serviceType}">${serviceType}</option>
				</c:forEach>			    
			</select>
		</div>
		
		<div id="div_customer_name" class="form-block">
			<span class="popup_label">Customer:</span>
			<input type="text" id="customer_name" class="popup_input field">
		</div>
		
		<div id="div_master_name" class="form-block">
			<span class="popup_label">Master:</span>
			<input type="text" id="master_name" class="popup_input field">
		</div>
		
		<div id="div_serviceProvided" class="form-block">
			<div class="flex-container">		
				<input type="checkbox" id="serviceProvided" class="checkbox field">
				<span class="popup_label4s">Service provided</span>
			</div>
		</div>
	</div>

	<script src="/js/datepicker-en.js"></script>
	<script src="/js/datepicker-uk.js"></script>
	<script src="/js/scheduler.js"></script>

<jsp:include page="/footer.jsp" />
var weekStartsDate;

function init() {
    if($("#table_schedule").length == 0) {
        return;
    }

	$("#date").datepicker();
	$("#date").datepicker("option", $.datepicker.regional[$("html").attr("lang")]);

	drawThisWeek();
}

function drawThisWeek() {
	weekStartsDate = startOfCurrentWeek(0);
	drawWeek();
}

function drawPrevWeek() {
	weekStartsDate = startOfCurrentWeek(-1);
	drawWeek();
}

function drawNextWeek() {
	weekStartsDate = startOfCurrentWeek(1);
	drawWeek();
}

function getISOFormattedLocalDate(date) {
	return dateFormat(date, "yyyy-mm-dd");
}

function getShortFormattedLocalDate(date) {
	return dateFormat(date, $("#date_format_short").val());
}

//NOTE: in our case first day is monday
//didn't want to use special js libraries to work with dates, so made simple transformation:
function dayOfWeek(date) {
	var weekStartShift = parseInt($("#week_shift").val());

	var day = new Date(date).getDay() - 1 - weekStartShift;
	if(day == -1) {
		day = 6;
	}
	return day;
}

function startOfCurrentWeek(shift) {
	var res; 
	
	if(shift == 0) {
		var today = Date.now();
		var day = dayOfWeek(today);
		res = today - day*24*3600*1000;
	} else {
		if(weekStartsDate == undefined) {
			weekStartsDate = startOfCurrentWeek(0);
		}
		res = weekStartsDate + shift*7*24*3600*1000;
	}
	
	return res;
}

function cleanSchedulerTable() {
	$(".table_schedule td").each(function(i, td) {
		$(td).html("");
	});		
}

function refreshHeader() {
	startShortDate = getShortFormattedLocalDate(new Date(weekStartsDate));
	endShortDate = getShortFormattedLocalDate(new Date(weekStartsDate + 6*24*3600*1000));

	$("#headerDates").text(startShortDate+" - "+endShortDate);
	
	for (d = 0; d <= 6; d++) {
		$("#d"+d).text(getShortFormattedLocalDate(new Date(weekStartsDate + d*24*3600*1000)));
	}
}

function drawWeek() {
	startDate = getISOFormattedLocalDate(new Date(weekStartsDate));
	endDate = getISOFormattedLocalDate(new Date(weekStartsDate + 6*24*3600*1000));
	
	refreshHeader();
	cleanSchedulerTable();
	
	var apiUrl = "/api/appointments/"+startDate+"/"+endDate;
	$.getJSON(apiUrl, function(responseJSON) {
		$.each(responseJSON, function(i, appointmentRecord) {
			drawAppointment(appointmentRecord.map);
		});
	}).fail(function (responseJSON) {
		showStatus("error", $("#error_serviceUnavailable").val());
	});
}

function validAppointment(appointmentData) {
	return appointmentData.hasOwnProperty("id") 
		&& appointmentData.hasOwnProperty("date")
		&& appointmentData.hasOwnProperty("time")
		&& appointmentData.hasOwnProperty("serviceType");
}

function cellContent(appointmentData) {
	var elementHTML = "";
	
	if(appointmentData.hasOwnProperty("master_name")) {
		elementHTML += $("#i18n_MASTER")[0].innerText + " <strong>"+appointmentData["master_name"] + "</strong><br>";
	}		
	if(appointmentData.hasOwnProperty("customer_name")) {
		elementHTML += $("#i18n_USER")[0].innerText + " <strong>"+appointmentData["customer_name"] + "</strong><br>";
	}		
	if(appointmentData.hasOwnProperty("serviceType")) {
		elementHTML += $("#i18n_service")[0].innerText + " <strong>" + $("#serviceType_"+appointmentData["serviceType"])[0].innerText + "</strong><br>";
	}		
	
	return elementHTML;
}

function drawAppointment(appointmentData) {
	if(!validAppointment(appointmentData)) {
		return;
	}
	
	var appointment_id = appointmentData["id"];
	var date = appointmentData["date"];
	var time = appointmentData["time"];
	
	var idOfCellToBind = "#d"+dayOfWeek(new Date(date))+"_"+time;
	
	var colorClass = " appointment_"+appointmentData["serviceType"].substring(0,1).toLowerCase();
	
	var newElement = $("<a/>", {
	    id: "appointment_"+appointment_id,
	    class: "appointment text_left"+colorClass,
	    href: "/",
		html: cellContent(appointmentData),
	    click: function() {
			openAppointment(appointmentData);
			return false;
		}
	}).appendTo($(idOfCellToBind));
}

function openAppointment(appointmentData) {
	if(appointmentData == null) {
        var workDayStarts = parseInt($("#WORK_TIME_STARTS").val());
        var workDayEnds = parseInt($("#WORK_TIME_ENDS").val());

        var appointmentDate = Date.now();

        var intTimeValue = parseInt(dateFormat(appointmentDate, "HH")) + 1;
        if(intTimeValue > workDayEnds) {
            intTimeValue = workDayStarts;
            appointmentDate += 24*3600*1000;
        } else if (intTimeValue < workDayStarts) {
            intTimeValue = workDayStarts;
        }

		appointmentData = {
			date: appointmentDate,
			rights_date: "W",
			time: intTimeValue,
			rights_time: "W",
			serviceType: "HAIRDRESSING",
			rights_serviceType: "W"
	    }
	}
	
	applyDataToFields(appointmentData);
	
	showAppointmentDialog();
}

function applyDataToFields(appointmentData) {
	$(".field").each(function(index, field) {
		var visible = false;
		var enabled = false;
		var dataValue = "";
		var ights = "";
		
		if(appointmentData.hasOwnProperty(field.id)) {
			dataValue = appointmentData[field.id];
			
			if(appointmentData.hasOwnProperty("rights_"+field.id)) {
				rights = appointmentData["rights_"+field.id];
				
				if(rights == "W") {
					visible = true;
					enabled = true;
				} else if(rights == "R") {
					visible = true;
				}
			}
		}
		
		setValueToElement(field.id, dataValue);
		setAccesibilityToElement(field.id, visible, enabled);
	});	
}

function setValueToElement(elementId, value) {
	$("#"+elementId).each(function(index, field) {
		if($(this)[0].type == "checkbox") {
			$(this).prop('checked', value == "true");
		} else {
			if(elementId == "date") {
			    value = dateFormat(value, $("#date_format_long").val())
			}
			$(this).val(value);
		}
	});
}

function setAccesibilityToElement(elementId, visible, enabled) {
	$("#div_"+elementId).prop("hidden", !visible);
	$("#"+elementId).prop("disabled", !enabled);
}

function reserveTime(dlg) {
	var appointmentDTO = {
        date: getISOFormattedLocalDate($("#date").val()),
        time: $("#time").val(),
        serviceType: $("#serviceType").val()
    }
	
	$.ajax({
        url: "/api/appointments",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        success: function (responseJSON) {
        	responseArray = responseJSON.split(":");
        	if(responseArray[0] == "success") {
        	    showStatus("success", "time is reserved"); //(!!!) i18n
                dlg.dialog("close");
                drawWeek();
        	} else {
        	    showStatus("error", responseArray[1]); //(!!!) i18n
        	}
        },
        error: function (responseJSON) {
            showStatus("error", $("#error_serviceUnavailable").val());
        },
        data: JSON.stringify(appointmentDTO)
    });

	return false;
}

function updateServiceProvided(appointmentId, serviceProvidedNewValue, dlg) {
	var appointmentDTO = {
            id: appointmentId,
            serviceProvided: "" + serviceProvidedNewValue
	    }
	
	$.ajax({
        url: "/api/appointments",
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        success: function (responseJSON) {
        	responseArray = responseJSON.split(":");
        	if(responseArray[0] == "success") {
        	    showStatus("success", "saved"); //(!!!) i18n
                dlg.dialog("close");
                drawWeek();
        	} else {
        	    showStatus("error", responseArray[1]); //(!!!) i18n
        	}
        },
        error: function (responseJSON) {
            showStatus("error", $("#error_serviceUnavailable").val());
			dlg.dialog("close");
        	drawWeek();
        },
        data: JSON.stringify(appointmentDTO)
    });
}

function showAppointmentDialog() {
	var setButtons = {};
	
	if($("input#id").val() == "") {
		setButtons[$("#i18n_reserve").val()] = function() {
			reserveTime($(this));
		};
	}
	
	var serviceProvidedVisible = !$("#div_serviceProvided")[0].hidden;
	var serviceProvidedEnabled = !$("input#serviceProvided")[0].disabled;
	var serviceProvidedCurrentValue = $("input#serviceProvided").prop("checked");
	
	if(serviceProvidedVisible&&serviceProvidedEnabled) {
		setButtons["Save"] = function() {
			var serviceProvidedNewValue = $("input#serviceProvided").prop("checked");
			
			if(serviceProvidedNewValue != serviceProvidedCurrentValue) {
				updateServiceProvided($("input#id").val(), serviceProvidedNewValue, $(this));
			}
		};
	}

	var btnCloseTitle = $("#i18n_close").val();
	if(Object.keys(setButtons).length > 0) {
		btnCloseTitle = $("#i18n_cancel").val();
	}
	setButtons[btnCloseTitle] = function() {
		$(this).dialog("close");
	};
	
	$(function() {
		$("#dialog-appointment").dialog({
			resizable: false,
			height: "auto",
			width: 600,
			modal: true,
			buttons: setButtons
		});
	});
}

function suggestRegistration() {
	var setButtons = {};

    setButtons[$("#i18n_login").text()] = function() {
        $(location).attr('href', '/login');
    };
    setButtons[$("#i18n_register").text()] = function() {
        $(location).attr('href', '/registration');
    };
    setButtons[$("#i18n_close").val()] = function() {
        $(this).dialog("close");
    };

	$(function() {
		$("#dialog-login").dialog({
			resizable: false,
			height: "auto",
			width: 400,
			modal: true,
			buttons: setButtons
		});
	});
}
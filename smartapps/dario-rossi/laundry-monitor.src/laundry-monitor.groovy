/**
 *  Laundry Monitor
 *
 *  Copyright 2016 Dario Rossi
 *  Based on from here: https://github.com/bmmiller/SmartThings/blob/master/smartapp.laundrymonitor/smartapp.laundrymonitor.groovy
 *  Changes:
 *  Added ability to show date timestamps for current cycle or if no cycle active, last cycle
 *  Added a trigger to run code oneat least one more time for wait time after wattage drops below threshold since if it doesn't change after that last drop below, it wouldn't end cycle
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.time.* 
 
definition(
    name: "Laundry Monitor",
    namespace: "dario.rossi",
    author: "Dario Rossi",
    description: "This application is a modification of the SmartThings Laundry Monitor SmartApp.  Instead of using a vibration sensor, this utilizes Power (Wattage) draw from an Aeon Smart Energy Meter.",
    category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartthings-device-icons/Appliances/appliances8-icn.png",
	iconX2Url: "https://s3.amazonaws.com/smartthings-device-icons/Appliances/appliances8-icn@2x.png")
    //iconUrl: "https://s3.amazonaws.com/smartapp-icons/FunAndSocial/App-HotTubTuner.png",
    //iconX2Url: "https://s3.amazonaws.com/smartapp-icons/FunAndSocial/App-HotTubTuner%402x.png")
	//iconUrl: "http://www.vivevita.com/wp-content/uploads/2009/10/recreation_sign_laundry.png",
    //iconX2Url: "http://www.vivevita.com/wp-content/uploads/2009/10/recreation_sign_laundry.png")


preferences {
	page(name: "mainPage", title: "Laundry Monitor Setup", install: true, uninstall: true)
}
def mainPage() {
	dynamicPage(name: "mainPage") {
        section("Laundry Status") {
            def currentLaundryStatus = ""
            def startDate = ""
            def stopDate = ""
            def emptyDate = ""
            def stoppedAtDateTime = "Unknown"
            def startedAtDateTime = "Unknown"
            def emptiedAtDateTime = "Unknown"
            def df = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a")
            df.setTimeZone(location.timeZone)

            if ( atomicState.startedAt != null ) {
                startDate = (atomicState.startedAt)
                startedAtDateTime = df.format(startDate)
            }
            if ( atomicState.stoppedAt != null ) {
                stopDate = (atomicState.stoppedAt)
                stoppedAtDateTime = df.format(stopDate)
            }
            if ( atomicState.emptiedAt != null ) {
                emptyDate = (atomicState.emptiedAt)
                emptiedAtDateTime = df.format(emptyDate)
            }

            if (atomicState.isRunning) {
                    currentLaundryStatus = "Cycle Active\nStarted at:${startedAtDateTime}\nPrevious stopped at: ${stoppedAtDateTime}\nPrevious empty at: ${emptiedAtDateTime}"
            }
            else {
                if (atomicState.isEmpty) {
                	currentLaundryStatus = "Cycle Not Active.\nLast cycle Info:\nStarted at ${startedAtDateTime}\nEnded at ${stoppedAtDateTime}\nEmptied at ${emptiedAtDateTime}"
				}
                else {
                	currentLaundryStatus = "Cycle Not Active.\nLast cycle Info:\nStarted at ${startedAtDateTime}\nEnded at ${stoppedAtDateTime}\nLoad needs to be emptied."
                }
            }
            paragraph currentLaundryStatus.trim()
        }	
        section("Tell me when this washer/dryer has stopped:") {
            input "selectedWasherOrDryer", "capability.powerMeter"
        }
        def anythingSet = anythingSet()
		if (anythingSet) {
			section("Use this to reset laundry complete notifications") {
				ifSet "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
				ifSet "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
				ifSet "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
				ifSet "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
				ifSet "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
				ifSet "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
				ifSet "button1", "capability.button", title: "Button Press", required:false, multiple:true
			}
		}
		def hideablesection = anythingSet || app.installationState == "COMPLETE"
		def sectionTitle = anythingSet ? "Select additional triggers" : "Use this to reset laundry complete notifications"
		
        section(sectionTitle, hideable: hideableSection, hidden: true) {
			ifUnset "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
			ifUnset "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
			ifUnset "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
			ifUnset "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
			ifUnset "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
			ifUnset "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
			ifUnset "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
		}

        section("Notifications") {
            input "sendPushMessage", "bool", title: "Push Notifications?"
            input "phone", "phone", title: "Send a text message?", required: false
            paragraph "For multiple SMS recipients, separate phone numbers with a semicolon(;)"      
        }
        section("Notification Messages") {
            input "sendStartMessage", "bool", title: "Send a cycle start message?", defaultValue: true, required: false
            input "startMessage", "text", title: "Cycle Started Message", description: "Cycle Start Message", defaultValue: "Laundry Cycle Started.", required: true
            input "sendCompleteMessage", "bool", title: "Send a cycle complete message?", defaultValue: true, required: false
            input "completionMessage", "text", title: "Cycle Ended Message", description: "Cycle Ended Message", defaultValue: "Laundry Cycle Complete.", required: true
        }
        section("System Variables") {
            input "minimumWattage", "decimal", title: "Minimum running wattage", required: false, defaultValue: 50
            input "minimumOffTime", "decimal", title: "Minimum amount of below wattage time to trigger off (secs)", required: false, defaultValue: 60
            input "repeatNotificationsTime", "decimal", title: "Time in between repeat notifications if laundry not emptied (secs)", required: false, defaultValue: 300
}
        section ("More Notification Options", hidden: hideOptionsSection(), hideable: true) {
            input "switches", "capability.switch", title: "Blink these switches:", required:false, multiple:true
            input "colorSwitches", "capability.colorControl", title: "Blink these color switches", required:false, multiple:true
            input "speech", "capability.speechSynthesis", title:"Announce messages via these devices: ", multiple: true, required: false
            input "sonos", "capability.musicPlayer", title: "Play messages on these speakers:", required: true
            input "resumePlaying", "bool", title: "Resume currently playing music after notification", required: false, defaultValue: true
            input "volume", "number", title: "Temporarily change volume", description: "0-100%", required: false, defaultValue: 50
        }
		section("Choose light effects...", hidden: colorSwitches, hideable: true)
		{
			input "color", "enum", title: "Color?", required: false, multiple:false, options: ["Red","Green","Blue","Yellow","Orange","Purple","Pink"]
			input "lightLevel", "enum", title: "Light Level?", required: false, options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]]
			input "duration", "number", title: "Duration Seconds?", required: false
		}

        section(mobileOnly:true) {
            label title: "Assign a name", required: false
            mode title: "Set for specific mode(s)", required: false
        }
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    log.debug "Installed with atomicState: ${atomicState}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
    log.debug "Updated with atomicState: ${atomicState}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(selectedWasherOrDryer, "power", powerInputHandler)
	subscribe(contact, "contact.open", resetNotificationHandler)
	subscribe(contactClosed, "contact.closed", resetNotificationHandler)
	subscribe(acceleration, "acceleration.active", resetNotificationHandler)
	subscribe(motion, "motion.active", resetNotificationHandler)
	subscribe(mySwitch, "switch.on", resetNotificationHandler)
	subscribe(mySwitchOff, "switch.off", resetNotificationHandler)
	subscribe(button1, "button.pushed", resetNotificationHandler)
	// Only check washing state if midcycle was on while app setting are being modified
    if (atomicState.midCycleCheck) {
    	log.debug "atomicState.midCycleCheck: ${atomicState.midCycleCheck}"
        runIn(minimumOffTime, checkWashingState)
	}
}


def checkWashingState() {
	def latestPower = selectedWasherOrDryer.currentValue("power")
    log.trace "Power: ${latestPower}W"
    
    if (!atomicState.isRunning && latestPower > minimumWattage) {
    	atomicState.isRunning = true
		atomicState.isEmpty = false
        atomicState.startedAt = now()
        atomicState.stoppedAt = null
        atomicState.emptiedAt = null
        atomicState.midCycleCheck = null
        log.info "Cycle started."
		if (sendStartMessage) {
        	sendNotifications(startMessage)
        }
	}
	else if (atomicState.isRunning && latestPower < minimumWattage) {
    	if (atomicState.midCycleCheck == null) {
			log.trace "Mid Cycle Check"
			atomicState.midCycleCheck = true
			atomicState.midCycleTime = now()
			runIn(minimumOffTime, checkWashingState)
		}
        else if (atomicState.midCycleCheck == true) {
			// Time between first check and now  
			if ((now() - atomicState.midCycleTime)/1000 > minimumOffTime) {
				atomicState.isRunning = false
                atomicState.midCycleCheck = false
                atomicState.stoppedAt = now()
                checkEmptyState
				log.info "startedAt: ${atomicState.startedAt}, stoppedAt: ${atomicState.stoppedAt}"                    
			}
            else {
				runIn(minimumOffTime, checkWashingState)
			}
		}
	}
}

def sendNotifications(notificationMessageText) {
	log.info notificationMessageText
	if (phone) {
		if ( phone.indexOf(";") > 1){
			def phones = phone.split(";")
			for ( def i = 0; i < phones.size(); i++) {
				sendSms(phones[i], notificationMessageText)
			}
		}
        else {
			sendSms(phone, notificationMessageText)
		}
	}
	if (sendPushMessage) {
		sendPush(notificationMessageText)
	}
	if (switches && !atomicState.isRunning ) {
		switchesNotifications
	}
    if (colorSwitches && !atomicState.isRunning ) {
		colorSwitchesNotifications
	}
	if (speech) { 
		speech.speak(notificationMessageText) 
	}
    if (notificationMessageText) {
		state.sound = textToSpeech(notificationMessageText instanceof List ? notificationMessageText[0] : notificationMessageText) // not sure why this is (sometimes) needed)
	}
    else {
		state.sound = textToSpeech("You selected the custom message option but did not enter a valid message in the ${app.label} Smart App")
	}
	if (resumePlaying){
		sonos.playTrackAndResume(state.sound.uri, state.sound.duration, volume)
	}
	else {
		sonos.playTrackAndRestore(state.sound.uri, state.sound.duration, volume)
	}
}

def switchesNotifications() {
	switches.each {
		state.previous[it.id] = [
			"switch": it.currentValue("switch"),
			"level" : it.currentValue("level")
		]
	}
    switches*.on()
    setTimer("resetNoColorSwitch")
}

def colorSwitchesNotifications() {

	def lightColor = 0
	if(color == "Blue")
		lightColor = 70//60
	else if(color == "Green")
		lightColor = 39//30
	else if(color == "Yellow")
		lightColor = 25//16
	else if(color == "Orange")
		lightColor = 10
	else if(color == "Purple")
		lightColor = 75
	else if(color == "Pink")
		lightColor = 83

	state.previous = [:]

	colorSwitches.each {
		state.previous[it.id] = [
			"switch": it.currentValue("switch"),
			"level" : it.currentValue("level"),
			"hue": it.currentValue("hue"),
			"saturation": it.currentValue("saturation"),
			"color": it.currentValue("color")			
		]
	}

	log.debug "current values = $state.previous"

	def newValue = [hue: lightColor, saturation: 100, level: (lightLevel as Integer) ?: 100]
	log.debug "new value = $newValue"

	colorSwitches*.setColor(newValue)
	setTimer("resetColorSwitch")
}

def setTimer(type)
{
	if(!duration) //default to 10 seconds
	{
		log.debug "pause 10"
		pause(10 * 1000)
		if (type == "resetNoColorSwitch") {
        	resetSwitches
		} else if (type == "resetColorSwitch") {
			resetColorSwitches
        }
    }
	else if(duration < 10)
	{
		log.debug "pause $duration"
		pause(duration * 1000)
		if (type == "resetNoColorSwitch") {
        	resetSwitches
		} else if (type == "resetColorSwitch") {
			resetColorSwitches
        }
	}
	else
	{
		log.debug "Reset switches or colorSwitches: runIn $duration, ${type}"
		if (type == "resetNoColorSwitch") {
        	runIn(duration,"resetSwitches", [overwrite: false])
		} else if (type == "resetColorSwitch") {
			runIn(duration,"resetColorSwitches", [overwrite: false])
        }
	}
}


def resetColorSwitches()
{
	huecolorSwitches.each {
		it.setColor(state.previous[it.id])        
	}
}

def resetSwitches()
{
	switches.each {
		it.switch(state.previous[it.id])        
	}
}


def checkEmptyState() {
	if (!atomicState.isRunning && !atomicState.isEmpty) {
    	if (sendCompletionMessage) {
			sendNotifications(completionMessage)
		}
		if (anythingSet) {
	    	runIn(repeatNotificationsTime, checkEmptyState)
    	}
	}
}

def powerInputHandler(evt) {
	checkWashingState
}

def resetNotificationHandler(evt) {
	if (!atomicState.isRunning && !atomicState.isEmpty) {
    	atomicState.isEmpty = true
        atomicState.emptiedAt = now()
	}
}

private hideOptionsSection() {
  (switches || switchesColor || speech || sonos) ? false : true
}

private anythingSet() {
	for (name in ["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","button1"]) {
		if (settings[name]) {
			return true
		}
	}
	return false
}

private ifUnset(Map options, String name, String capability) {
	if (!settings[name]) {
		input(options, name, capability)
	}
}

private ifSet(Map options, String name, String capability) {
	if (settings[name]) {
		input(options, name, capability)
	}
}

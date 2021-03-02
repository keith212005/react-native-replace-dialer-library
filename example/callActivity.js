import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';
import App from './src';

import React, {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  DeviceEventEmitter,
  SafeAreaView,
  TouchableHighlight,
} from 'react-native';
import {CustomButton, CallTimer, image} from './src';
import ReplaceDialer from 'react-native-replace-dialer';

import CallDetectorManager from 'react-native-call-detection';

import CountDown from 'react-native-countdown-component';
import {Stopwatch, Timer} from 'react-native-stopwatch-timer';

export default class CallActivity extends Component {
  constructor(props) {
    super(props);
    this.state = {
      connected: false,
      callType: '',
      phoneNumber: '',
      bluetoothName: 'Bluetooth',
      speakerOn: false,
      microphone: false,
      pause: false,
    };
    this.startListenerTapped();
    ReplaceDialer.getBluetoothName((name) => {
      this.setState({bluetoothName: name});
    });
  }

  // call state start
  startListenerTapped() {
    this.callDetector = new CallDetectorManager(
      (event, phoneNumber) => {
        this.setState({connected: true, callType: event});
        // For iOS event will be either "Connected",
        // "Disconnected","Dialing" and "Incoming"

        // For Android event will be either "Offhook",
        // "Disconnected", "Incoming" or "Missed"
        // phoneNumber should store caller/called number

        console.log('startListenerTapped2 >> ', event, phoneNumber);
        if (event === 'Disconnected') {
          // Do something call got disconnected
          ReplaceDialer.closeCurrentView();
        } else if (event === 'Connected') {
          // Do something call got connected
          // This clause will only be executed for iOS
        } else if (event === 'Incoming') {
          // Do something call got incoming
        } else if (event === 'Dialing') {
          // Do something call got dialing
          // This clause will only be executed for iOS
        } else if (event === 'Offhook') {
          //Device call state: Off-hook.
          // At least one call exists that is dialing,
          // active, or on hold,
          // and no calls are ringing or waiting.
          // This clause will only be executed for Android
        } else if (event === 'Missed') {
          ReplaceDialer.closeCurrentView();
          // Do something call got missed
          // This clause will only be executed for Android
        }
      },
      false, // if you want to read the phone number of the incoming call [ANDROID], otherwise false
      () => {}, // callback if your permission got denied [ANDROID] [only if you want to read incoming number] default: console.error
      {
        title: 'Phone State Permission',
        message:
          'This app needs access to your phone state in order to react and/or to adapt to incoming calls.',
      }, // a custom permission request message to explain to your user, why you need the permission [recommended] - this is the default one
    );
  }

  incomingView = () => {
    return (
      <View style={styles.incomingViewContainer}>
        <TouchableOpacity
          style={{flexDirection: 'column'}}
          onPress={() => this.handleAcceptCall()}>
          <Image
            style={styles.receiveCall}
            source={{uri: image.recieveCallButton}}
          />
          <Text style={{textAlign: 'center'}}>Accept</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
          <Text style={{textAlign: 'center'}}>Decline</Text>
        </TouchableOpacity>
      </View>
    );
  };

  callAnsweredView = () => {
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
        }}>
        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
        </TouchableOpacity>
      </View>
    );
  };

  handleSpeaker = () => {
    const {speakerOn} = this.state;
    this.setState({speakerOn: !speakerOn});
    ReplaceDialer.toggleSpeakerOnOff();
  };

  handleMic = () => {
    const {microphone} = this.state;
    this.setState({microphone: !microphone});
    ReplaceDialer.toggleMicOnOff();
  };

  handleBluetooth = () => {
    ReplaceDialer.toggleBluetoothOnOff();
  };

  // Answer Call
  handleAcceptCall = () => {
    console.log('accept call pressed');
    ReplaceDialer.acceptCall();
    this.setState({showTimer: true});
  };

  //Call end
  endCall = () => {
    ReplaceDialer.disconnectCall();
    this.setState({showTimer: false});
  };

  render() {
    const {speakerOn, microphone, pause, showTimer, callType} = this.state;
    return (
      <SafeAreaView style={styles.container}>
        {/* Show timer when user pick up call */}
        {showTimer ? (
          <CallTimer />
        ) : (
          <Text style={styles.calling}>Calling...</Text>
        )}

        <Text style={styles.calling}>{this.state.phoneNumber}</Text>
        <View style={styles.row}>
          <CustomButton
            name="Add"
            imageUri={image.plus_black}
            imageStyle={{height: '23%'}}
          />
          <CustomButton
            name="Pause"
            imageUri={pause ? image.pause_black : image.pause_gray}
            imageStyle={{height: '23%'}}
          />
          <CustomButton
            name={this.state.bluetoothName}
            imageUri={image.bluetooth_gray}
            imageStyle={{height: '23%'}}
            onPress={this.handleBluetooth}
          />
        </View>
        <View style={styles.row}>
          <CustomButton
            name="Speaker"
            imageUri={speakerOn ? image.speaker_black : image.speaker_gray}
            imageStyle={{height: '23%'}}
            onPress={this.handleSpeaker}
          />

          <CustomButton
            name="Mute"
            imageUri={microphone ? image.mic_gray : image.mic_black}
            imageStyle={{height: '23%'}}
            onPress={this.handleMic}
          />

          <CustomButton
            name="Keypad"
            imageUri={image.keypad_black}
            imageStyle={{height: '23%'}}
          />
        </View>

        {/*
        {this.incomingView()}
        */}

        {callType === 'Incoming' || callType === ''
          ? this.incomingView()
          : this.callAnsweredView()}
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  endCall: {
    height: '28%',
    aspectRatio: 1,
  },
  receiveCall: {
    height: '28%',
    aspectRatio: 1,
  },
  calling: {
    paddingTop: 10,
    fontSize: 22,
    color: 'gray',
  },
  row: {
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-evenly',
    alignItems: 'center',
  },
  incomingViewContainer: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-evenly',
  },
});

const options = {
  container: {
    backgroundColor: '#000',
    padding: 5,
    borderRadius: 5,
    width: 220,
  },
  text: {
    fontSize: 30,
    color: '#FFF',
    marginLeft: 7,
  },
};

AppRegistry.registerComponent(appName, () => CallActivity);

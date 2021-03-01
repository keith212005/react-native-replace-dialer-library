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

import {CustomButton, CallTimer} from '@components';
import {image} from '@constants';
import {responsiveFonts} from '@resources';

import ReplaceDialer from 'react-native-replace-dialer';
import CallState from 'react-native-call-state';
import CountDown from 'react-native-countdown-component';
import {Stopwatch, Timer} from 'react-native-stopwatch-timer';

export default class CallScreen extends Component {
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

  componentDidMount() {
    // const {phoneNumber} = this.props.route.params;
    // if (phoneNumber != '') {
    //   ReplaceDialer.callPhoneNumber(phoneNumber, (message) => {
    //     console.log(message);
    //   });
    // }
  }

  // call state start
  startListenerTapped() {
    CallState.startListener();
    DeviceEventEmitter.addListener('callStateUpdated', (data) => {
      console.log('Call state updated>>', data);
      var event = data.state;
      this.setState({callType: event});
      if (
        event === 'Connected' ||
        event === 'Incoming' ||
        event === 'Dialing' ||
        event === 'Offhook'
      ) {
        this.setState({connected: true, callType: event});
      } else {
        this.setState({connected: false, callType: ''});
        if (event === 'Disconnected') {
          this.props.navigation.pop();
        }
      }
    });
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
  };

  render() {
    const {speakerOn, microphone, pause, showTimer, callType} = this.state;
    // const {phoneNumber} = this.props.route.params;
    return (
      <SafeAreaView style={styles.container}>
        {/* Show timer when user pick up call */}
        {showTimer ? (
          <CallTimer />
        ) : (
          <>
            <Text style={styles.calling}>Calling...</Text>
            {/*
            <Text style={styles.phoneNumber}>
              {phoneNumber ? phoneNumber : ''}
            </Text>
            */}
            <Text style={styles.calling}>{callType ? callType : ''}</Text>
          </>
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

        {callType != 'Incoming' ? this.callAnsweredView() : this.incomingView()}
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
  phoneNumber: {
    fontSize: responsiveFonts(40),
  },
});

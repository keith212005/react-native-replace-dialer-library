import React, {Component} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  DeviceEventEmitter,
  SafeAreaView,
  StatusBar,
} from 'react-native';

import {CustomButton, CallTimer} from '@components';
import {image} from '@constants';
import {responsiveFonts, responsiveHeight, responsiveWidth} from '@resources';

import ReplaceDialer from 'react-native-replace-dialer';

import CallDetectorManager from 'react-native-call-detection';
import CountDown from 'react-native-countdown-component';
import {Stopwatch, Timer} from 'react-native-stopwatch-timer';

export default class CallScreen extends Component {
  constructor(props) {
    super(props);
    console.log('phone$$$', props.route.params.outGoingNumber);
    const {outGoingNumber} = this.props.route.params;
    this.state = {
      connected: false,
      callType: '',
      startTimer: true,
      showTimer: false,
      phoneNumber: outGoingNumber ? outGoingNumber : '',
      bluetoothName: 'Bluetooth',
      speakerOn: false,
      holdCall: false,
      microphone: false,
      callRecord: false,
      showKeypad: false,
      addCall: false,
    };
    this.startListenerTapped();
    ReplaceDialer.getBluetoothName((name) => {
      this.setState({bluetoothName: name});
    });
  }

  stopListenerTapped() {
    this.callDetector && this.callDetector.dispose();
  }

  componentDidMount() {}

  componentWillUnmount() {
    this.stopListenerTapped();
  }

  startListenerTapped() {
    this.callDetector = new CallDetectorManager(
      (event, phoneNumber) => {
        this.setState({callType: event});

        // For iOS event will be either "Connected",
        // "Disconnected","Dialing" and "Incoming"

        // For Android event will be either "Offhook",
        // "Disconnected", "Incoming" or "Missed"
        // phoneNumber should store caller/called number

        console.log('startListenerTapped2 >> ', event, phoneNumber);
        if (event === 'Disconnected') {
          // Do something call got disconnected
          this.setState({connected: false, startTimer: false}, () => {
            setTimeout(() => {
              ReplaceDialer.closeCurrentView();
            }, 1000);
          });
        } else if (event === 'Connected') {
          // Do something call got connected
          // This clause will only be executed for iOS
          console.log('Connected...');
          this.setState({connected: true});
        } else if (event === 'Incoming') {
          // Do something call got incoming
          console.log('incoming...');
          this.setState({connected: false});
        } else if (event === 'Dialing') {
          // Do something call got dialing
          // This clause will only be executed for iOS
          console.log('dialing...');
          this.setState({connected: false});
        } else if (event === 'Offhook') {
          //Device call state: Off-hook.
          // At least one call exists that is dialing,
          // active, or on hold,
          // and no calls are ringing or waiting.
          // This clause will only be executed for Android
          console.log('offhook...');
          this.setState({connected: true});
        } else if (event === 'Missed') {
          // Do something call got missed
          // This clause will only be executed for Android
          console.log('missed...');
          this.setState({connected: false});
          setTimeout(() => {
            ReplaceDialer.closeCurrentView();
          }, 1000);
        }
      },
      true, // if you want to read the phone number of the incoming call [ANDROID], otherwise false
      (number) => {
        console.log('permission denied', number);
      }, // callback if your permission got denied [ANDROID] [only if you want to read incoming number] default: console.error
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
          <Text style={{textAlign: 'center', marginTop: 5}}>Accept</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={this.endCall}>
          <Image style={styles.endCall} source={{uri: image.endCallButton}} />
          <Text style={{textAlign: 'center', marginTop: 5}}>Decline</Text>
        </TouchableOpacity>
      </View>
    );
  };

  callAnsweredView = () => {
    return (
      <TouchableOpacity onPress={this.endCall}>
        <Image style={styles.endCall} source={{uri: image.endCallButton}} />
      </TouchableOpacity>
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

  handleCallRecord = () => {
    const {callRecord} = this.state;
    this.setState({callRecord: !callRecord});
  };

  handleBluetooth = () => {
    ReplaceDialer.toggleBluetoothOnOff();
  };

  handleShowKeypad = () => {
    const {showKeypad} = this.state;
    this.setState({showKeypad: !showKeypad});
  };

  handleAddCall = () => {
    ReplaceDialer.makeConferenceCall('8160626880');
  };

  handleHoldCall = () => {
    const {holdCall} = this.state;
    this.setState({holdCall: !holdCall}, () => {
      ReplaceDialer.holdCall(this.state.holdCall);
    });
  };

  // Accept Call
  handleAcceptCall = () => {
    this.setState({showTimer: true}, () => {
      ReplaceDialer.acceptCall();
    });
  };

  // Decline call
  endCall = () => {
    ReplaceDialer.disconnectCall();
  };

  render() {
    const {
      speakerOn,
      microphone,
      showTimer,
      startTimer,
      callType,
      callRecord,
      phoneNumber,
      showKeypad,
      holdCall,
      addCall,
    } = this.state;

    return (
      <>
        <StatusBar barStyle={'dark-content'} />
        <SafeAreaView style={styles.container}>
          <View style={styles.subContainer1}>
            {callType === 'Dialing' || callType === 'Offhook' ? (
              <Text style={styles.calling}>Calling...</Text>
            ) : (
              <Text style={styles.calling}>{callType}</Text>
            )}

            <Text style={styles.phoneNumber}>
              {phoneNumber ? phoneNumber : ''}
            </Text>

            {/* Show timer when user pick up call */}
            {showTimer ? <CallTimer startTimer={startTimer} /> : null}
          </View>

          <View style={styles.subContainer2}>
            <View style={styles.row}>
              <CustomButton
                name="Add"
                imageUri={addCall ? image.plus_black : image.plus_gray}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleAddCall}
                buttonStyle={{padding: 30}}
              />
              <CustomButton
                name="Hold"
                imageUri={holdCall ? image.pause_gray : image.pause_black}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleHoldCall}
                buttonStyle={{padding: 30}}
              />
              <CustomButton
                name="Record"
                imageUri={callRecord ? image.record_black : image.record_gray}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleCallRecord}
                buttonStyle={{padding: 30}}
              />
              <CustomButton
                name={this.state.bluetoothName}
                imageUri={image.bluetooth_gray}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleBluetooth}
                buttonStyle={{padding: 30}}
              />
            </View>
            <View style={styles.row}>
              <CustomButton
                name="Speaker"
                imageUri={speakerOn ? image.speaker_black : image.speaker_gray}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleSpeaker}
                buttonStyle={{padding: 30}}
              />

              <CustomButton
                name="Mute"
                imageUri={microphone ? image.mic_gray : image.mic_black}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleMic}
                buttonStyle={{padding: 30}}
              />

              <CustomButton
                name="Keypad"
                imageUri={showKeypad ? image.keypad_black : image.keypad_gray}
                imageStyle={{height: responsiveHeight(5)}}
                onPress={this.handleShowKeypad}
                buttonStyle={{padding: 30}}
              />
            </View>
          </View>

          <View style={styles.subContainer3}>
            {callType === 'Incoming' || callType === 'Missed'
              ? this.incomingView()
              : this.callAnsweredView()}
          </View>
        </SafeAreaView>
      </>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: responsiveHeight(10),
    alignItems: 'center',
    justifyContent: 'center',
  },
  endCall: {
    height: responsiveHeight(8),
    aspectRatio: 1,
  },
  receiveCall: {
    height: responsiveHeight(8),
    aspectRatio: 1,
  },
  calling: {
    textAlign: 'center',
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
    fontSize: responsiveFonts(26),
  },
  subContainer1: {
    flex: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  subContainer2: {
    flex: 6,
    justifyContent: 'center',
    alignItems: 'center',
  },
  subContainer3: {
    flex: 2,
  },
});

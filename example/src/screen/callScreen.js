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

    this.state = {
      connected: false,
      callType: '',
      startTimer: true,
      showTimer: false,
      phoneNumber: '',
      bluetoothName: 'Bluetooth',
      speakerOn: false,
      microphone: false,
      pause: false,
    };
    // this.startListenerTapped();
    this.startListenerTapped();
    ReplaceDialer.getBluetoothName((name) => {
      this.setState({bluetoothName: name});
    });
  }

  stopListenerTapped() {
    this.callDetector && this.callDetector.dispose();
  }

  componentWillUnmount() {
    this.stopListenerTapped();
  }

  startListenerTapped() {
    this.callDetector = new CallDetectorManager(
      (event, phoneNumber) => {
        this.setState({callType: event, phoneNumber: phoneNumber});
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
            }, 2000);
          });
        } else if (event === 'Connected') {
          // Do something call got connected
          // This clause will only be executed for iOS
          this.setState({connected: true});
        } else if (event === 'Incoming') {
          // Do something call got incoming
          this.setState({connected: false});
        } else if (event === 'Dialing') {
          // Do something call got dialing
          // This clause will only be executed for iOS
          this.setState({connected: false});
          console.log('Dialing >> ', event, phoneNumber);
        } else if (event === 'Offhook') {
          //Device call state: Off-hook.
          // At least one call exists that is dialing,
          // active, or on hold,
          // and no calls are ringing or waiting.
          // This clause will only be executed for Android
          this.setState({connected: true});
        } else if (event === 'Missed') {
          // Do something call got missed
          // This clause will only be executed for Android
          this.setState({connected: false});
          setTimeout(() => {
            ReplaceDialer.closeCurrentView();
          }, 2000);
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

  handleBluetooth = () => {
    ReplaceDialer.toggleBluetoothOnOff();
  };

  // Answer Call
  handleAcceptCall = () => {
    this.setState({showTimer: true}, () => {
      ReplaceDialer.acceptCall();
    });
  };

  //Call end
  endCall = () => {
    ReplaceDialer.disconnectCall();
  };

  render() {
    const {
      speakerOn,
      microphone,
      pause,
      showTimer,
      startTimer,
      callType,
      phoneNumber,
    } = this.state;
    console.log('calT>>', callType);
    // const {phoneNumber} = this.props.route.params;
    return (
      <>
        <StatusBar barStyle={'dark-content'} />
        <SafeAreaView style={styles.container}>
          <Text style={styles.phoneNumber}>
            {phoneNumber ? phoneNumber : ''}
          </Text>

          {/* Show timer when user pick up call */}
          {showTimer ? (
            <CallTimer startTimer={startTimer} />
          ) : (
            <>
              {callType === 'Dialing' || callType === 'Offhook' ? (
                <Text style={styles.calling}>Calling...</Text>
              ) : null}

              <Text style={styles.calling}>{callType ? callType : ''}</Text>
            </>
          )}
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

          {callType === 'Incoming' || callType === 'Missed'
            ? this.incomingView()
            : this.callAnsweredView()}
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
});

package apps.wazzzainvst.miai.utilities.Jsyn;

import com.jsyn.*;          // JSyn and Synthesizer classes
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.scope.AudioScope;
import com.jsyn.unitgen.*;  // Unit generators like SineOscillator


public class MyJSYN {

    public static void main(String[] args) {

        Synthesizer synth = JSyn.createSynthesizer();
        synth.start();

  //      myNoise.output.connect( myFilter.input );

        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start( 44100, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels, AudioDeviceManager.USE_DEFAULT_DEVICE,
                numOutputChannels );

        LineOut myOut;
        WhiteNoise myNoise;
        FilterStateVariable myFilter;
        synth.add( myOut = new LineOut() );
        synth.add( myNoise = new WhiteNoise() );
        synth.add( myFilter = new FilterStateVariable() );

        myNoise.output.connect( myFilter.input );

        myFilter.output.connect( 0, myOut.input, 0 ); /* Left side */
        myFilter.output.connect( 0, myOut.input, 1 ); /* Right side */

        SineOscillator myOsc = new SineOscillator();
        myOsc.frequency.set( 440.0 );  // 440 Hz
        myOsc.amplitude.set( 0.5 );  // Half amplitude.

        myOut.start();

        AudioScope scope = new AudioScope(synth);
        scope.addProbe( myOsc.output ); // display multiple signals
        scope.addProbe( myOsc.output );
// Trigger on a threshold level vs AUTO trigger.
        scope.setTriggerMode( AudioScope.TriggerMode.NORMAL );
        scope.start();
    }
}

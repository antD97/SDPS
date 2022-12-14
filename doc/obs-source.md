# OBS Text Source

SDPS has an additional feature of being able to actively update a text file with combat information
that [OBS](https://obsproject.com/) can actively display on screen. If you prefer a video format,
the video demonstration at the bottom of this page covers everything here.

Thanks to [Purdze](https://www.twitch.tv/Purdze) for the idea for this feature.

## Usage

1. To enable this feature, simple click the "OBS Text Source" button and check "Enable OBS File
Source". SDPS will create a `obs-source.txt` file that will be actively updated while that checkbox
is enabled.

2. In OBS, create a new source and select "Text (GDI+)".

3. In the properties window for the text source:
    1. Enable the checkbox for "Read from file".
    2. Click the "Browse" button so select the `obs-source.txt` file which will be located next to
       the `SDPS-X.X.X.exe` file.
    3. Click the "Select font" button and select a
       [monospaced font](https://en.wikipedia.org/wiki/Monospaced_font) with a large font size.
       Lucida Console, Courier New, Courier, and Consolas should all be available by default on
       Windows.
    4. Add any additional personal styling such as a translucent background color.

When SDPS adds rows to the table in the main window, the OBS text source will be updated along with
it. On reset, the OBS text source will appear as empty.

Settings in the main window will be mimicked to the OBS text source. For example, toggling columns
will likewise toggle them in the text source. The "Auto-Reset" toggle works the same way.

The SDPS "OBS Text Source Settings" window also provides some additional settings:
- Print Headers: toggles the first row in the text source that labels each column
- Print Totals Row: toggles the last row in the text source that totals the damage, mitigated, heal
  received, and heal applied rows
- Column Width: adjusts the widths of the columns in the text source (excluding the reason column)
- Reason Column Width: adjusts the width of the reason column in the text source
- Max Lines: adjusts how many lines to display to the text source

## OBS Text Overdrive Script

By default, the rate at which OBS reads from the source text file is fairly slow. A workaround to
update the source faster is to use a custom OBS script.

1. Open my [text-overdrive.lua](https://gist.github.com/antD97/f12e9f38cdd872b9481a31c20f5a6ae1)
   gist and click "Download ZIP" in the top right corner.

2. Extract the `text-overdrive.lua` file from the downloaded zip.

3. In OBS, select the "Tools" menu at the top and then select "Scripts".

4. Click the + icon in the new window and select the `text-overdrive.lua` file.

5. In the "Text Source" field, enter the name of your text source *exactly* as it appears in the
   sources list.

6. Set the "Interval (ms)" field to 250. This setting adjusts how quickly the text source is
   updated. Values too high feel slow and laggy, while values too low can make the stream/recording
   drop frames and appear laggy.

## Video Demonstration



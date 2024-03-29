# Reading the Federalist Papers 
#
# A Fed object contains one Federalist paper
# (Right now it has only the number)
#
class Fed
    attr_accessor :fedno

    # Constructor
    def initialize
      @fedno=0
    end

    # Method to print data on one Fed object
    def prt
       puts "Federalist #@fedno"
    end
end

#=========================
# Main program
#=========================

# Input will come from file fed.txt
file = File.new("./fed.txt", "r")

# List of Fed objects 
feds = []

# Read and process each line
while (line = file.gets)
    line.strip!            # Remove trailing white space
    words = line.split     # Split into array of words
    if words.length == 0 then
        next
    end

    # "FEDERALIST No. number" starts a new Fed object
    if words[0]=="FEDERALIST" then
       curFed = Fed.new    # Construct new Fed object
       feds << curFed      # Add it to the array
       curFed.fedno = words[2]
       next
    end
end # End of reading

file.close

# Apply the prt (print) method to each Fed object in the feds array
feds.each{|f| f.prt}


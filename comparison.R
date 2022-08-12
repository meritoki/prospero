
library("TSclust")
args <- commandArgs(trailingOnly = TRUE)

##### EXAMPLE 2.1 INTEREST RATES DATASET - PREDICTION BASED DISTANCE #####

# load the dataset
#data("interest.rates")
data <- read.csv(args)
data[, colSums(data != 0) > 0]
interest.rates <- ts(data,start=c(1980,1), end=c(1990,12), frequency=12)

# since the dissimilarity is bootstrap based, set a seed for reproducibility
set.seed(35000)

# sample plot of the prediction densities from which the distance is calculated,
# the prediction is done at an horizon h=6 steps

#diss.PRED(interest.rates[, 13], interest.rates[, 16], h = 6, B = 20000, logarithm.x = TRUE,
#  logarithm.y = TRUE, differences.x = 1, differences.y = 1, plot = T)$L1dist

# prepare the correct differences and logarithms for all the countries involved
# in the dataset
#diffs <- rep(1, ncol(interest.rates))
#logs <- rep(TRUE, ncol(interest.rates))

#set.seed(74748)
# compute the distance at for the dataset, high computational requirements
#dpred <- diss(interest.rates, "PRED", h = 6, B = 1200, logarithms = logs, differences = diffs,
#  plot = T)
# preform hierarchical clustering and plot the dendogram
#hc.dpred <- hclust(dpred$dist)

#plot(hc.dpred, main = "", sub = "", xlab = "", ylab = "")


##### EXAMPLE 2.2 INTEREST RATES DATASET - DISSIMILARITY COMPARISON #####

# data are properly transformed
relative.rate.change <- diff(log(interest.rates), 1)
relative.rate.change[is.na(relative.rate.change)] <- 0
#print(relative.rate.change)
# 5-cluster solutions will be stored in 'Five.cluster.sol'' matrix
Five.cluster.sol <- matrix(0, nrow = ncol(relative.rate.change), ncol = 3)
colnames(Five.cluster.sol) <- c("ACF", "LNP", "PIC")
rownames(Five.cluster.sol) <- colnames(relative.rate.change)
# obtaining the 5-cluster solutions with diss.ACF, diss.PER and diss.AR.PIC
Five.cluster.sol[, 1] <- cutree(hclust(diss(relative.rate.change, "ACF", p = 0.05)),
  k = 8)
Five.cluster.sol[, 2] <- cutree(hclust(diss(relative.rate.change, "PER", normalize = TRUE,
  logarithm = TRUE)), k = 8)
Five.cluster.sol[, 3] <- cutree(hclust(diss(relative.rate.change, "AR.PIC")), k = 8)
# show the solution
Five.cluster.sol
